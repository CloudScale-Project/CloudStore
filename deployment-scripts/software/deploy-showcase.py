import time
import paramiko
import ConfigParser
import sys, os
import subprocess
from common.Cloudscale import *

class DeploySoftware:

    def __init__(self, config_file, cfg, key_name, key_pair):
        self.cfg = cfg
        self.file_path = "/".join(os.path.abspath(__file__).split('/')[:-1])
        self.ip_addresses = self.cfg.get('infrastructure', 'ip_address').split(",")
        self.mysql_version = '%s/showcase/target/showcase-1.0.0-BUILD-SNAPSHOT.war' % str(os.path.sep).join(os.path.abspath(__file__).split(os.path.sep)[:-1])
        self.nosql_version = self.cfg.get('sofware', 'nosql_war_path')
        self.remote_user = self.cfg.get('infrastructure', 'remote_user')
        self.key_pair = self.cfg.get('EC2', 'key_pair')
        self.remote_deploy_path = self.cfg.get('software', 'remote_deploy_path')
        self.write_db_config()
        self.compile()
        self.deploy_software()

    def write_db_config(self):
        path = os.path.abspath(self.file_path + '/showcase/src/main/resources/database/database.aws.hibernate.properties')
        f = open(path, 'w')
        f.write('jdbc.dbtype=mysql\n')
        f.write('jdbc.driverClassName=com.mysql.jdbc.ReplicationDriver\n')
        f.write('jdbc.url=jdbc:mysql:replication://%s/%s\n' % (self.cfg.get('platform', 'urls'), self.cfg.get('RDS', 'database_name')))
        f.write('jdbc.username=%s\n' % self.cfg.get('RDS', 'database_user'))
        f.write('jdbc.password=%s\n' % self.cfg.get('RDS', 'database_pass'))
        f.write('jdbc.hibernate.dialect=org.hibernate.dialect.MySQLDialect\n')
        f.close()

    def compile(self):
        ps = subprocess.Popen('cd ' + self.file_path + '/showcase;/usr/local/bin/mvn -Pamazon-hibernate install', shell=True)
        ps.wait()

    def deploy_software(self):
        for ip_address in self.ip_addresses:
            if sys.platform == 'win32':
                self.windows_shell(ip_address);
            else:
                self.unix_shell(ip_address)

    def windows_shell(self,ip_address):
        raise Exception('Not implemented for Windows platform!')

    def unix_shell(self, ip_address):
        print "Deploying showcase on " + ip_address
        print "This may take a while. Please wait ..."
        #time.sleep(30)

        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        if key_pair:
            ssh.connect(ip_address, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
        else:
            ssh.connect(ip_address, username=self.remote_user)

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        scp.put(self.mysql_version, 'showcase-1-a.war')
        #scp.put(self.nosql_version, 'showcase-1-b.jar')

        #ssh.exec_command("sudo unzip showcase-1-a.war -d showcase-1-a")
        ssh.exec_command("sudo cp -r showcase-1-a.war " + self.remote_deploy_path)
        ssh.exec_command("sudo /etc/init.d/tomcat7 restart")

        print "Successfully finished installation"

    def parse_config_file(self, config_file):
        f = open(config_file, 'r')
        ip_addresses = []
        for line in f.readlines():
            ip_addresses.append(line)
        return ip_addresses



if __name__ == '__main__':
    check_args(1, "<config_path>")
    config_file, cfg, key_name, key_pair = parse_args()

    DeploySoftware(config_file, cfg, key_name, key_pair)
