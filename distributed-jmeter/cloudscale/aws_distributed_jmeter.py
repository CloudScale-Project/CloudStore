import boto, boto.ec2

import sys, os, time
import paramiko
import subprocess
import select
import logging
import thread
from threading import Thread
from .common.distributed_jmeter import DistributedJmeter
from scripts.meet_sla_req import check
logger = logging.getLogger(__name__)

class CreateInstance(DistributedJmeter):

    def __init__(self, cfg, scenario_path):
        super(CreateInstance, self).__init__(scenario_path)
        self.scenario_path = scenario_path
        self.key_pair = cfg.get('EC2', 'key_pair')
        self.key_name = cfg.get('EC2', 'key_name')
        self.cfg = cfg
        self.pid = str(scenario_path.split('/')[-1][:-4])
        self.conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        self.create_security_groups()

        masters = []
        for i in xrange(int(self.cfg.get('EC2', 'num_jmeter_slaves'))):
            instance = self.create_instance("Creating master instance {0} ...".format(i+1))
	    time.sleep(30)
            self.log(instance.ip_address)
	    self.setup_master(instance)
            masters.append(instance)

	self.run_masters(masters)

    def setup_master(self, instance):
        ip_addr = instance.ip_address

	    ssh = self.ssh_to_instance(ip_addr)

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        dirname = os.path.abspath(os.path.dirname(__file__))
        _, stdout, _ = ssh.exec_command('rm -rf /home/ubuntu/*')
        stdout.readlines()

        self.log("Transfering jmeter_master.tar.gz ...")
        scp.put( dirname + '/../scripts/jmeter_master.tar.gz', '/home/ubuntu/jmeter.tar.gz')


        self.log("Transfering JMeter scenario ...")
        scp.put( self.scenario_path, 'scenario.jmx')

        self.log("Unpacking JMeter ...")
        _, stdout, _ = ssh.exec_command("tar xvf jmeter.tar.gz")
        stdout.readlines()

        _, stdout, _ = ssh.exec_command("find . -iname '._*' -exec rm -rf {} \;")
        stdout.readlines()

    def ssh_to_instance(self, ip_addr):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        if self.key_pair:
            ssh.connect(ip_addr, username="ubuntu", key_filename=os.path.abspath(self.key_pair))
        else:
            ssh.connect(ip_addr, username="ubuntu", password="")
	return ssh

    def run_masters(self, instances):

        tmp_userpath = "/tmp/{0}".format(os.path.basename(self.scenario_path)[:-4])
	
        dirname = os.path.abspath(os.path.dirname(__file__))
        resultspath = "{0}/../static/results/".format(dirname)
	
        if not os.path.exists(tmp_userpath):
            os.makedirs(tmp_userpath, 0777)
	
        self.log(resultspath)

        for instance in instances:
	    self.log("Running JMeter on instance %s" % instance.ip_address)
	    ssh = self.ssh_to_instance(instance.ip_address)
            cmd = "(~/jmeter/bin/jmeter -n -t ~/scenario.jmx -l scenario.jtl -j scenario.log -Jstartup_threads=%s -Jrest_threads=%s -Jhost=%s;touch finish)" % (self.cfg.get('EC2', 'startup_threads'), self.cfg.get('EC2', 'rest_threads'), self.cfg.get('EC2', 'host'))
	    self.log(cmd)
            self.log("Executing your JMeter scenario. This can take a while. Please wait ...")
            stdin, stdout, stderr = ssh.exec_command(cmd)

        i = 1
        threads = []
        for instance in instances:
            t = Thread(target=self.check_instance, args=(i, tmp_userpath, resultspath, instance))
            t.start()
            threads.append(t)
            i+=1
	
        for t in threads:
            t.join()
	
        for instance in instances:	    
            self.conn.terminate_instances(instance_ids=[instance.id])
	
	
        cmd = "cp -r {0} {1}".format(tmp_userpath, resultspath)
        self.log(cmd)
        p = subprocess.check_output(cmd.split())
	
        resultspath = resultspath + os.path.basename(self.scenario_path)[:-4]
        filenames = ["{0}/scenario{1}.log".format(resultspath, j) for j in xrange(1,i)]
        self.log(filenames)
        with open("{0}/scenario.log".format(resultspath), 'w') as outfile:
            for fname in filenames:
                with open(fname) as infile:
                    for line in infile:
                        outfile.write(line)
	
       	filenames = ["{0}/response-times-over-time{1}.csv".format(resultspath, j) for j in xrange(1, i)]
        self.log(filenames)
        with open("{0}/response-times-over-time.csv".format(resultspath), 'w') as outfile:
            for fname in filenames:
                with open(fname) as infile:
                    for line in infile:
                        outfile.write(line)

        self.log("<br>".join(check("{0}/response-times-over-time.csv".format(resultspath)).split('\n')))

        self.log("Finished!", fin=True)	
	

    def check_instance(self, i, tmp_userpath, resultspath, instance):
        cmd = "cat finish"

        ssh = self.ssh_to_instance(instance.ip_address)
        _, _, stderr = ssh.exec_command(cmd)

        while len(stderr.readlines()) > 0:
            time.sleep(30)
            ssh.close()
            ssh = self.ssh_to_instance(instance.ip_address)
            _, _, stderr = ssh.exec_command(cmd)
	
        self.log("Finishing thread " + str(i))

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.log("JMeter scenario finished. Collecting results")
        scp.get("/home/ubuntu/scenario.log", "{0}/{1}".format(tmp_userpath, "scenario" + str(i) + ".log"))
        scp.get("/home/ubuntu/scenario.jtl", "{0}/{1}".format(tmp_userpath, "scenario" + str(i) + ".jtl"))
        scp.get("/home/ubuntu/response-times-over-time.csv", "{0}/{1}".format(tmp_userpath, "response-times-over-time" + str(i) + ".csv"))
        scp.close()
        ssh.close()
		                
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
        res = self.conn.run_instances(self.cfg.get('EC2', 'ami_id'), key_name=self.key_name, instance_type=self.cfg.get('EC2','instance_type'),security_groups=['cs-jmeter', 'ssh', 'flask'])
        time.sleep(30)
        self.wait_available(res.instances[0])
        instance = self.conn.get_all_instances([res.instances[0].id])[0].instances[0]
        return instance

    def wait_available(self, instance):
        self.log( "Waiting for instance to become available" )
        self.log( "Please wait ..." )
        status = self.conn.get_all_instances(instance_ids=[instance.id])[0].instances[0].state
        i=1
        while status != 'running':
            if i%10 == 0:
                self.log( "Please wait ..." )
            status = self.conn.get_all_instances(instance_ids=[instance.id])[0].instances[0].state
            time.sleep(3)
            i=i+1
        self.log( "Instance is up and running" )


    def write_config(self, config_path, instance):
        self.cfg.save_option(config_path, 'infrastructure', 'remote_user', 'ubuntu')
        self.cfg.save_option(config_path, 'infrastructure', 'ip_address', instance.ip_address)

def read_config(config_file):
    cfg = boto.Config()
    cfg.load_from_path(os.path.abspath(config_file))

    return cfg
