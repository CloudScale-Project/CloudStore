import paramiko
import os
import subprocess
from .common.distributed_jmeter import DistributedJmeter
import novaclient.v1_1 as novaclient

class OpenStackDistributedJmeter(DistributedJmeter):

    def __init__(self, scenario_path, cfg):
        super(OpenStackDistributedJmeter, self).__init__(scenario_path)
        self.cfg = cfg

        self.host = self.cfg.get('OPENSTACK', 'host')
        self.startup_threads = self.cfg.get('OPENSTACK', 'startup_threads')
        self.rest_threads = self.cfg.get('OPENSTACK', 'rest_threads')
        self.num_jmeter_slaves = self.cfg.get('OPENSTACK', 'num_jmeter_slaves')

        master_ip = self.create_instance('jmeter-master')
        slaves_ips = [self.create_instance('jmeter-slave-%s' % i) for i in range(self.num_jmeter_slaves) ]
        self.run_jmeter(master_ip, slaves_ips, scenario_path)

    def create_instance(self, name):
        nc = novaclient.Client(
            self.cfg.get('OPENSTACK', 'user'),
            self.cfg.get('OPENSTACK', 'pwd'),
            self.cfg.get('OPENSTACK', 'tenant'),
            auth_url=self.cfg.get('OPENSTACK', 'url'))

        nc.servers.create(name, self.cfg.get('OPENSTACK', 'image'), self.cfg.get('OPENSTACK', 'flavor'))
        for server in nc.servers.list():
            if server._info['name'] == name:
                return server

    def run_jmeter(self, master_ip, slave_ips, scenario_path):

        self.log(scenario_path)
        username = "distributedjmeter"
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        ssh.connect(master_ip, username=username, password='password')
        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())

        self.log("Transfering scenario to OpenStack ...")

        dirname = os.path.abspath(os.path.dirname(__file__))
        scp.put( scenario_path, 'scenario.jmx')

        self.log("Executing JMeter scenario on OpenStack ...")

        cmd = "~/jmeter/bin/jmeter -n -t ~/scenario.jmx -j scenario.log -R %s -Ghost=%s -Gstartup_threads=%s -Grest_threads=%s" % (",".join(slave_ips), self.host, self.startup_threads, self.rest_threads)
        _, stdout, _ = ssh.exec_command(cmd)
        stdout.readlines()

        resultspath = "{0}/../static/results/".format(dirname)

        tmp_userpath = "/tmp/{0}".format(os.path.basename(scenario_path)[:-4])
        os.makedirs(tmp_userpath, 0777)
        scp.get("/home/{2}/scenario.log", "{0}/{1}".format(tmp_userpath, "scenario.log", username))
        scp.get("/home/{2}/response-times-over-time.csv", "{0}/{1}".format(tmp_userpath, "response-times-over-time.csv", username))

        cmd = "cp -r {0} {1}".format(tmp_userpath, resultspath)
        p = subprocess.check_output(cmd.split())

        scp.close()
        ssh.close()

        self.log("Finished! You can now download report files.", 1)
