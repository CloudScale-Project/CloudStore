
import time
import paramiko
import ConfigParser
import sys, os

class InstallTomcatApache:

    def __init__(self, config_path, cfg, key_name, key_pair):
        self.config_path = config_path
        self.cfg = cfg
        self.ip_addresses = self.cfg.get('infrastructure', 'ip_address').split(",")
        self.remote_user = self.cfg.get('infrastructure', 'remote_user')
        self.key_pair = key_pair
        self.setup_instance()
        self.write_config()

    def setup_instance(self):
        for ip_address in self.ip_addresses:
            if sys.platform == 'win32':
                self.windows_shell(ip_address)
            else:
                self.unix_shell(ip_address)

    def windows_shell(self, ip_address):
        raise Exception('Not implemented for Windows platform!')

    def unix_shell(self, ip_address):
        print "Installing needed software on " + ip_address
        print "This may take a while. Please wait ..."
        time.sleep(60)

        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())


        if key_pair:
            ssh.connect(ip_address, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
        else:
            ssh.connect(ip_address, username=self.remote_user)

        working_dir = str(os.path.sep).join(os.path.abspath(__file__).split(os.path.sep)[:-1])

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        scp.put(os.path.abspath('%s%scloudscale-vm-setup.sh' % (working_dir, os.path.sep)), 'cloudscale-vm-setup.sh')
        scp.put(os.path.abspath('%s%scloudscale-apache-virtualhost.conf' % (working_dir, os.path.sep)), 'cloudscale.conf')

        _, stdout, _ = ssh.exec_command("sudo sh cloudscale-vm-setup.sh")
        stdout.readlines()
        print "Successfully finished installation"

    def parse_config_file(self, config_file):
        f = open(config_file, 'r')
        ip_addresses = []
        for line in f.readlines():
            ip_addresses.append(line)
        return ip_addresses

    def write_config(self):
        self.cfg.save_option(self.config_path, 'software', 'remote_deploy_path', '/var/lib/tomcat7/webapps/')
        self.cfg.save_option(self.config_path, 'software', 'mysql_war_path', 'showcase-1-a.war')
        self.cfg.save_option(self.config_path, 'software', 'nosql_war_path', 'showcase-1-b.war')
        # f = open(os.path.abspath('../software.ini'), 'w')
        # f.write('[Cloudscale]\n')
        # f.write('remote_user='+ self.remote_user +'\n')
        # f.write('ip_addresses=' +  ','.join(self.ip_addresses) + '\n')
        # f.write('key_pair=' + (self.key_pair if self.key_pair else ''))
        # f.write('remote_deploy_path=/var/lib/tomcat7/webapps/')
        # f.write('mysql_war_path=showcase-1-a.war')
        # f.write('nosql_war_path=showcase-1-b.war')
        # f.close()


if __name__ == '__main__':
    print __file__
    print str(os.path.sep).join(os.path.abspath(__file__).split(os.path.sep)[:-1])
    check_args(1, "<config_path>")

    config_path, cfg, key_name, key_pair =  parse_args()

    InstallTomcatApache(config_path, cfg, key_name, key_pair)
