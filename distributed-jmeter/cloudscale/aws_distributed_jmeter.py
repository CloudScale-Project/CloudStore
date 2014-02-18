import boto, boto.ec2
import sys, os, time
import paramiko
import subprocess
import logging
from cloudscale import models

logger = logging.getLogger(__name__)

class CreateInstance:

    def __init__(self, config_path, cfg, key_pair, key_name, scenario_path, num_slaves):
        self.scenario_path = scenario_path
        self.num_slaves = num_slaves
        self.key_pair = key_pair
        self.key_name = key_name
        self.cfg = cfg
        self.pid = str(scenario_path.split('/')[-1][:-4])
        self.conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        self.create_security_groups()

        slaves = []
        for i in xrange(int(self.cfg.get('EC2', 'num_jmeter_slaves'))):
            instance = self.create_instance("Creating slave instance {0} ...".format(i+1))
            slaves.append(instance)

        self.log("Please wait one minute for status checks ...")
        time.sleep(60) # wait for status checks
        self.log("Setting up slaves ...")
        self.setup_slaves(slaves)
        instance = self.create_instance("Creating master instance ... ")
        self.log("Please wait one minute for status checks ...")
        time.sleep(60) # wait for status checks
        self.log("Setting up master ...")
        self.setup_master(slaves, instance)
        #self.write_config(config_path, instance)

    def log(self, msg, fin=0):
        logger.info(msg)
        db_log = models.Log()
        db_log.process_id = self.pid
        db_log.log = msg
        db_log.finished = fin
        db_log.save()

    def clear(self):
        msgs = models.Log.objects.filter(process_id=self.pid)
        for obj in msgs:
            obj.delete()

    def setup_master(self, slaves, instance):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        #ip_addr = '54.194.221.83'
        ip_addr = instance.ip_address

        if self.key_pair:
            ssh.connect(ip_addr, username="ubuntu", key_filename=os.path.abspath(self.key_pair))
        else:
            ssh.connect(ip_addr, username="ubuntu", password="root")

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        dirname = os.path.abspath(os.path.dirname(__file__))

        self.log("Transfering jmeter_master.tar.gz ...")
        scp.put( dirname + '/../scripts/jmeter_master.tar.gz', 'jmeter.tar.gz')

        self.log("Transfering JMeter scenario ...")
        scp.put( self.scenario_path, 'scenario.jmx')

        self.log("Installing Java 7 on master ...")
        _, stdout, _ = ssh.exec_command("sudo apt-get -y install openjdk-7-jdk; tar xvf jmeter.tar.gz")
        stdout.readlines()

        ip_addresses = [instance.private_ip_address for instance in slaves]
        # ip_addresses = ['172.31.31.9', '172.31.26.205']
        cmd = "~/jmeter/bin/jmeter -n -t ~/scenario.jmx -R %s -l scenario.jtl -j scenario.log" % ",".join(ip_addresses)
        self.log("Executing your JMeter scenario. This can take a while. Please wait ...")
        stdin, stdout, stderr = ssh.exec_command(cmd)
        # wait for JMeter to execute
        stdout.readlines()

        # get reports
        resultspath = "{0}/../static/results/".format(dirname)

        tmp_userpath = "/tmp/{0}".format(os.path.basename(self.scenario_path)[:-4])
        os.makedirs(tmp_userpath, 0777)
        scp.get("/home/ubuntu/scenario.log", "{0}/{1}".format(tmp_userpath, "scenario.log"))
        scp.get("/home/ubuntu/scenario.jtl", "{0}/{1}".format(tmp_userpath, "scenario.jtl"))

        cmd = "cp -r {0} {1}".format(tmp_userpath, resultspath)
        p = subprocess.check_output(cmd.split())

        scp.close()
        ssh.close()

        self.log("Finished! You can now download report files.", 1)
        instance_ids = [inst.id for inst in slaves] + [instance.id]
        self.conn.terminate_instances(instance_ids=instance_ids)

    def setup_slaves(self, instances):
        for instance in instances:
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

            if self.key_pair:
                ssh.connect(instance.ip_address, username="ubuntu", key_filename=os.path.abspath(self.key_pair))
            else:
                ssh.connect(instance.ip_address, username="ubuntu", password="root")

            scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
            dirname = os.path.abspath(os.path.dirname(__file__))
            self.log("Transfering jmeter_slave.tar.gz ...")
            scp.put( dirname + '/../scripts/jmeter_slave.tar.gz', 'jmeter.tar.gz')

            self.log( "Installing Java 7 on slave ..." )
            _, stdout, _ = ssh.exec_command("sudo apt-get -y install openjdk-7-jdk; tar xvf jmeter.tar.gz")
            stdout.readlines()

            self.log("Starting jmeter-server ...")
            ssh.exec_command("~/jmeter/bin/jmeter-server &")
            ssh.close()
            scp.close()

    def create_security_groups(self):
        self.log( "Creating security groups ..." )
        self.create_security_group('cs-jmeter', 'Security group for JMeter', '8557', '0.0.0.0/0')
        self.add_security_group_rule('cs-jmeter', 'tcp', '1099', '0.0.0.0/0')
        # self.create_security_group('http', 'Security group for HTTP protocol', '80', '0.0.0.0/0')
        self.create_security_group('ssh', 'Security group for HTTP protocol', '22', '0.0.0.0/0')

    def create_security_group(self, name, description, port, cidr):
        try:
            self.conn.create_security_group(name, description)
            self.conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise

    def add_security_group_rule(self, group_name, protocol, port, cidr):
        try:
            group = self.conn.get_all_security_groups(groupnames=[group_name])[0]
            group.authorize(protocol, port, port, cidr)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidPermission.Duplicate':
                raise


    def create_instance(self, msg = "Creating EC2 instance"):
        self.log(msg)
        res = self.conn.run_instances(self.cfg.get('EC2', 'ami_id'), key_name=self.key_name, instance_type=self.cfg.get('EC2','instance_type'),security_groups=['cs-jmeter', 'ssh'])
        self.wait_available(res.instances[0])
        instance = self.conn.get_all_instances([res.instances[0].id])[0].instances[0]
        return instance

    def wait_available(self, instance):
        self.log( "Waiting for instance to become available" )
        self.log( "Please wait ..." )
        status = self.conn.get_all_instances([instance.id])[0].instances[0].state
        i=1
        while status != 'running':
            if i%10 == 0:
                self.log( "Please wait ..." )
            status = self.conn.get_all_instances([instance.id])[0].instances[0].state
            time.sleep(3)
            i=i+1
        self.log( "Instance is up and running" )


    def write_config(self, config_path, instance):
        self.cfg.save_option(config_path, 'infrastructure', 'remote_user', 'ubuntu')
        self.cfg.save_option(config_path, 'infrastructure', 'ip_address', instance.ip_address)
        # f = open(os.path.abspath('../infrastructure.ini'), 'w')
        # f.write('[EC2]\n')
        # f.write('remote_user=ubuntu\n')
        # f.write('ip_address=' + instance.ip_address + '\n')
        # f.close()

def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(os.path.abspath(config_file))

    return cfg

def usage(args):
    print 'Usage:\n $ python %s %s' % (sys.argv[0].split("/")[-1], args)

def check_args(num_args, args_desc):
    if len(sys.argv) < num_args+1:
        usage(args_desc)
        exit(0)

def parse_args():
    config_file = sys.argv[1]

    if not os.path.isfile(config_file):
        print config_file + ' doesn\'t exist!'
        exit(0)

    cfg = read_config(config_file)
    key_name = cfg.get('EC2', 'key_name')
    key_pair = os.path.abspath(cfg.get('EC2', 'key_pair'))
    if not os.path.isfile(key_pair):
        print key_pair + ' doesn\'t exist!'
        exit(0)

    return config_file, cfg, key_name, key_pair


if __name__ == "__main__":
    check_args(4, "<config_path> <scenario_path> <num_virtual_users> <num_slaves>")
    config_path, cfg, key_name, key_pair = parse_args()
    CreateInstance(config_path, cfg, key_pair, key_name, sys.argv[2], sys.argv[3], sys.argv[4])
