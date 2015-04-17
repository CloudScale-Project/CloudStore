import re
import os
import time
import datetime
import subprocess
import select
from threading import Thread
import shutil

import boto.rds
import boto
import boto.ec2
import boto.ec2.cloudwatch
import boto.ec2.autoscale
from boto.exception import EC2ResponseError
import paramiko


from cloudscale.distributed_jmeter.scripts.meet_sla_req import check
from cloudscale.distributed_jmeter.scripts.visualization.visualize import Visualize


class AWS:
    def __init__(self, cfg, scenario_path, r_path, output_path, logger, test=False):
        self.cfg = cfg
        self.r_path = r_path
        self.logger = logger
        self.scenario_path = scenario_path
        self.output_directory = output_path
        if not test:
            self.init()
            self.start()

    def init(self):
        self.key_name = self.cfg.get('EC2', 'key_name')
        self.startup_threads = self.cfg.get('TEST', 'startup_threads')
        self.rest_threads = self.cfg.get('TEST', 'rest_threads')
        self.host = self.cfg.get('SHOWCASE', 'host')
        self.user = self.cfg.get('EC2', 'remote_user')
        self.jmeter_url = self.cfg.get('SCENARIO', 'jmeter_url')
        self.region = self.cfg.get('AWS', 'region')
        self.access_key = self.cfg.get('AWS', 'aws_access_key_id')
        self.secret_key = self.cfg.get('AWS', 'aws_secret_access_key')
        self.num_jmeter_slaves = int(self.cfg.get('TEST', 'num_jmeter_slaves'))
        self.frontend_instances_identifier = self.cfg.get('SHOWCASE', 'frontend_instances_id')
        self.rds_identifiers = self.cfg.get('RDS', 'identifiers').split(',')
        self.is_autoscalable = True if self.cfg.get('SHOWCASE', 'autoscalable') == 'yes' else False
        self.num_threads = int(self.cfg.get('SCENARIO', 'num_threads'))
        self.instance_type = self.cfg.get('EC2', 'instance_type')
        self.ami_id = self.cfg.get('EC2', 'ami_id')
        self.scenario_duration = self.cfg.get('SCENARIO', 'duration_in_minutes')

        self.conn = boto.ec2.connect_to_region(
            self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )

        self.key_pair = self.create_keypair()

        self.create_security_groups()

    def start(self):
        ips = []
        for i in xrange(self.num_jmeter_slaves):
            instance = self.create_instance("Creating master instance {0} ...".format(i + 1))
            time.sleep(15)
            self.logger.log(instance.ip_address)
            self.setup_master(instance.ip_address)
            ips.append(instance.ip_address)

        self.run_masters(ips)

    def setup_master(self, ip_addr):
        ssh = self.ssh_to_instance(ip_addr)

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        dirname = os.path.abspath(os.path.dirname(__file__))
        _, stdout, _ = ssh.exec_command('rm -rf /home/' + self.user + '/*')
        stdout.readlines()

        self.logger.log("Transfering jmeter_master.tar.gz ")
        _, stdout, _ = ssh.exec_command("wget -q -T90 %s -O jmeter.tar.gz" % self.jmeter_url)
        self.wait_for_command(stdout)

        self.logger.log("Transfering JMeter scenario ...")
        scp.put(self.scenario_path, 'scenario.jmx')

        self.logger.log("Unpacking JMeter ...")
        _, stdout, _ = ssh.exec_command("tar xvf jmeter.tar.gz")
        self.wait_for_command(stdout)

        _, stdout, _ = ssh.exec_command("find . -iname '._*' -exec rm -rf {} \;")
        self.wait_for_command(stdout)

    def wait_for_command(self, stdout, verbose=False):
        # Wait for the command to terminate
        while not stdout.channel.exit_status_ready():
            # Only print data if there is data to read in the channel
            if stdout.channel.recv_ready():
                rl, wl, xl = select.select([stdout.channel], [], [], 0.0)
                if len(rl) > 0:
                    response = stdout.channel.recv(1024)
                    if verbose:
                        print response


    def ssh_to_instance(self, ip_addr, i=0):
        try:
            if i < 3:
                ssh = paramiko.SSHClient()
                ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
                if self.key_pair:
                    ssh.connect(ip_addr, username=self.user, key_filename=os.path.abspath(self.key_pair))
                else:
                    ssh.connect(ip_addr, username=self.user, password="")
                return ssh
            raise Exception('Failed 3 times to SSH to %s' % ip_addr)
        except Exception as e:
            self.logger.log('%s\nTrying to reconnect ...' % e.message)
            time.sleep(30)
            return self.ssh_to_instance(ip_addr, i=i + 1)


    def run_masters(self, ips):
        start_time = datetime.datetime.utcnow()

        tmp_userpath = "/tmp/{0}".format(os.path.basename(self.scenario_path)[:-4])

        if not os.path.exists(tmp_userpath):
            os.makedirs(tmp_userpath, 0777)

        self.logger.log(self.output_directory)

        for ip in ips:
            self.logger.log("Running JMeter on instance %s" % ip)
            ssh = self.ssh_to_instance(ip)
            cmd = "(~/jmeter/bin/jmeter -n -t ~/scenario.jmx -l scenario.jtl -j scenario.log -Jall_threads=%s -Jstartup_threads=%s -Jrest_threads=%s -Jhost=%s;touch finish)" % (
                int(self.startup_threads)+int(self.rest_threads),
                self.startup_threads,
                self.rest_threads,
                self.host
            )
            self.logger.log(cmd)
            self.logger.log("Executing your JMeter scenario. This can take a while. Please wait ...")
            ssh.exec_command(cmd)
            ssh.close()

        i = 1
        threads = []
        for ip in ips:
            self.logger.log("Starting thread for %s" % ip)
            t = Thread(target=self.check_instance, args=(i, tmp_userpath, self.output_directory, ip))
            t.start()
            threads.append(t)
            i += 1

        for t in threads:
            t.join()

        self.terminate_instances(ips)
        end_time = datetime.datetime.utcnow()

        instances = self.get_instances_by_tag('Name', self.frontend_instances_identifier);
        instance_ids = [instance.id for instance in instances]
        rds_instance_ids = self.rds_identifiers
        ec2_data = self.get_cloudwatch_ec2_data(start_time, end_time, instance_ids)
        rds_data = self.get_cloudwatch_rds_data(start_time, end_time, rds_instance_ids)

        resultspath = self.output_directory

        cmd = "cp -r {0}/./ {1}/".format(tmp_userpath, resultspath)
        self.logger.log(cmd)
        p = subprocess.check_output(cmd.split())

        shutil.rmtree(tmp_userpath, True)

        filenames = ["{0}/scenario{1}.log".format(resultspath, j) for j in xrange(1, i)]
        self.logger.log(filenames)
        with open("{0}/scenario.log".format(resultspath), 'w') as outfile:
            for fname in filenames:
                with open(fname) as infile:
                    for line in infile:
                        outfile.write(line)
        cmd = "rm -rf %s" % " ".join(filenames)
        subprocess.call(cmd.split())
        cmd = "rm -rf %s/*.jtl" % resultspath
        subprocess.call(cmd.split())

        filenames = ["{0}/response-times-over-time{1}.csv".format(resultspath, j) for j in xrange(1, i)]
        self.logger.log(filenames)
        with open("{0}/response-times-over-time.csv".format(resultspath), 'w') as outfile:
            for fname in filenames:
                with open(fname) as infile:
                    for line in infile:
                        outfile.write(line)

        cmd = "rm -rf %s" % " ".join(filenames)
        subprocess.call(cmd.split())

        filename = "{0}/ec2-cpu.csv".format(resultspath)
        with open(filename, 'w') as fp:
            fp.write("instance_id,timestamp,average\n")
            for row in ec2_data:
                for data in row.get('data'):
                    fp.write("%s,%s,%s\n" % (row.get('instance_id'), self.unix_time(data['Timestamp']), data['Average']))

        filename = "{0}/rds-cpu.csv".format(resultspath)
        with open(filename, 'w') as fp:
            fp.write("instance_id,timestamp,average\n")
            for row in rds_data:
                for data in row.get('data'):
                    fp.write("%s,%s,%s\n" % (row.get('instance_id'), self.unix_time(data['Timestamp']), data['Average']))

        if self.is_autoscalable:
            activites = self.get_autoscalability_data(start_time, end_time)
            self.write_autoscalability_data(resultspath, activites)
        else:
            self.write_autoscalability_data(resultspath, [])

        slo_output = check("{0}/response-times-over-time.csv".format(resultspath))
        self.logger.log("<br>".join(slo_output).split('\n'))
        self.logger.log("Visualizing....")
        v = Visualize(self.num_threads, self.scenario_duration, self.r_path,
                      "{0}/response-times-over-time.csv".format(resultspath),
                      "{0}/autoscalability.log".format(resultspath))
        v.save()

        self.logger.log("finished!", fin=True)
        with open("{0}/finish".format(resultspath), "w") as fp:
            fp.write("finish")

    def unix_time(self, dt):

        epoch = datetime.datetime.fromtimestamp(0)
        delta = dt - epoch
        return delta.total_seconds()

    def unix_time_millis(self,dt):
        return self.unix_time(dt) * 1000.0

    def get_autoscalability_data(self, start_time, end_time):
        def get_action(activity):
            if activity.description.lower().find('terminating') > -1:
                return 'terminate'
            return 'launch'

        def filter_activites(activites):
            filtered_activites = []
            for activity in activites:
                instance_id = re.search('(i-.*)', activity.description.lower()).group(1)

                a = {
                    'instance_id': instance_id,
                    'start_time': activity.start_time + datetime.timedelta(hours=1),
                    'end_time': activity.end_time + datetime.timedelta(hours=1),
                    'action': get_action(activity)
                }
                filtered_activites.append(a)
            return filtered_activites

        def closest_activity(closest_activity, activites):
            for i in xrange(1, len(activites)):
                activity = activites[i]
                if activity['end_time'] > closest_activity['end_time'] and activity['end_time'] < start_time and \
                                activity['action'] == 'launch' and activity['instance_id'] not in terminating_ids:
                    closest_activity = activity
            return closest_activity

        autoscale = boto.ec2.autoscale.connect_to_region(
            self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )
        activites = autoscale.get_all_activities('distributed_jmeter-as')
        launching_ids = []
        terminating_ids = []
        new_activites = filter_activites(activites)
        filtered_activites = []
        for activity in new_activites:
            if activity['end_time'] > start_time:
                filtered_activites.append(activity)

            if activity['action'] == 'terminate':
                terminating_ids.append(activity['instance_id'])

        # closest_activity_id = set(launching_ids) - set(terminating_ids) # get activites that were not terminated

        f_a = []
        for a in filtered_activites:
            for a2 in filtered_activites:
                if (a['action'] == 'terminate' and a2['instance_id'] == a['instance_id'] and a2[
                    'action'] == 'launch') or (a['action'] == 'launch' and a2['instance_id'] == a['instance_id']):
                    f_a.append(a)

        return f_a
        # return [closest_activity] + f_a

    def terminate_instances(self, ips):
        reservations = self.conn.get_all_instances()
        for res in reservations:
            for instance in res.instances:
                if instance.ip_address in ips:
                    self.conn.terminate_instances(instance_ids=[instance.id])

    def check_instance(self, i, tmp_userpath, resultspath, ip):
        cmd = "cat finish"

        ssh = self.ssh_to_instance(ip)
        _, _, stderr = ssh.exec_command(cmd)

        while len(stderr.readlines()) > 0:
            time.sleep(10)
            ssh.close()
            ssh = self.ssh_to_instance(ip)
            _, _, stderr = ssh.exec_command(cmd)

        self.logger.log("finishing thread " + str(i))
        ssh.close()
        ssh = self.ssh_to_instance(ip)
        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("jmeter scenario finished. collecting results")
        scp.get("/home/{0}/scenario.log".format(self.user),
                "{0}/{1}".format(tmp_userpath, "scenario" + str(i) + ".log"))
        # scp.get("/home/{0}/scenario.jtl".format(self.user),
        #         "{0}/{1}".format(tmp_userpath, "scenario" + str(i) + ".jtl"))
        scp.get("/home/{0}/response-times-over-time.csv".format(self.user),
                "{0}/{1}".format(tmp_userpath, "response-times-over-time" + str(i) + ".csv", self.user))
        # scp.get("/home/{0}/results-tree.xml".format(self.user), "{0}/{1}".format(tmp_userpath, "results-tree" + str(i) + ".xml", self.user))
        scp.close()
        ssh.close()

    def create_security_groups(self):
        self.logger.log("creating security groups ...")
        self.create_security_group('cs-jmeter', 'security group for jmeter', '8557', '0.0.0.0/0')
        self.add_security_group_rule('cs-jmeter', 'tcp', '1099', '0.0.0.0/0')
        # self.create_security_group('http', 'security group for http protocol', '80', '0.0.0.0/0')
        self.create_security_group('ssh', 'security group for http protocol', '22', '0.0.0.0/0')

    def create_security_group(self, name, description, port, cidr):
        try:
            self.conn.create_security_group(name, description)
            self.conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port,
                                               cidr_ip=cidr)
        except EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise

    def add_security_group_rule(self, group_name, protocol, port, cidr):
        try:
            group = self.conn.get_all_security_groups(groupnames=[group_name])[0]
            group.authorize(protocol, port, port, cidr)
        except EC2ResponseError as e:
            if str(e.error_code) != 'InvalidPermission.Duplicate':
                raise


    def create_instance(self, msg="creating ec2 instance"):
        self.logger.log(msg)
        res = self.conn.run_instances(self.ami_id, key_name=self.key_name,
                                      instance_type=self.instance_type,
                                      security_groups=['cs-jmeter', 'ssh', 'flask'])
        time.sleep(30)
        self.wait_available(res.instances[0])
        instance = self.conn.get_all_instances([res.instances[0].id])[0].instances[0]
        return instance

    def wait_available(self, instance):
        self.logger.log("waiting for instance to become available")
        self.logger.log("please wait ...")
        status = self.conn.get_all_instances(instance_ids=[instance.id])[0].instances[0].state
        i = 1
        while status != 'running':
            if i % 10 == 0:
                self.logger.log("please wait ...")
            status = self.conn.get_all_instances(instance_ids=[instance.id])[0].instances[0].state
            time.sleep(10)
            i = i + 1
        self.logger.log("instance is up and running")


    def write_config(self, config_path, instance):
        self.cfg.save_option(config_path, 'infrastructure', 'remote_user', 'ubuntu')
        self.cfg.save_option(config_path, 'infrastructure', 'ip_address', instance.ip_address)

    def write_autoscalability_data(self, resultspath, activites):
        with open("{0}/autoscalability.log".format(resultspath), "w") as fp:
            fp.write('"instance_id","start_time","end_time","action"\n')
            for activity in activites:
                fp.write('%s,%s,%s,%s\n' % (
                activity['instance_id'], str(activity['start_time']).split(".")[0], activity['end_time'],
                activity['action']))

    def create_keypair(self):
        try:
            keypair = self.conn.create_key_pair(self.key_name)
        except EC2ResponseError as e:
            if e.error_code == 'InvalidKeyPair.Duplicate':
                self.conn.delete_key_pair(key_name=self.key_name)
                keypair = self.conn.create_key_pair(self.key_name)
            else:
                raise e

        keypair.save(self.output_directory)
        return "%s/%s.pem" % (self.output_directory, self.key_name)

    def get_cloudwatch_ec2_data(self, start_time, end_time, instance_ids):
        conn = boto.ec2.cloudwatch.connect_to_region(
            self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )
        data = []
        for instance_id in instance_ids:
            data.append({
                'instance_id': instance_id,
                'data': conn.get_metric_statistics(
                    60,
                    start_time,
                    end_time,
                    'CPUUtilization',
                    'AWS/EC2',
                    'Average',
                    dimensions={'InstanceId': [instance_id]}
                )
            })

        return data

    def get_cloudwatch_rds_data(self, start_time, end_time, instance_ids):
        conn = boto.ec2.cloudwatch.connect_to_region(
            self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )
        data = []
        for instance_id in instance_ids:
            data.append({
                'instance_id': instance_id,
                'data': conn.get_metric_statistics(
                    60,
                    start_time,
                    end_time,
                    'CPUUtilization',
                    'AWS/RDS',
                    'Average',
                    dimensions={'DBInstanceIdentifier': [instance_id]}
                )
            })

        return data

    def get_instance_ids_from_ip(self, ips):
        instance_ids = []
        for ip in ips:
            instances = self.conn.get_only_instances()
            for instance in instances:
                if instance.ip_address == ip and instance:
                    instance_ids.append(instance.id)
                    break
        return instance_ids


    def get_instances_by_tag(self, tag, value):
        reservations = self.conn.get_all_instances()
        my_instances = []
        for res in reservations:
            for instance in res.instances:
                if tag in instance.tags and instance.tags[tag] == value:
                    my_instances.append(instance)
        return my_instances