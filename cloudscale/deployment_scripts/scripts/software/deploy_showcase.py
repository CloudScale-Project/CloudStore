import logging
import time
import paramiko
import sys
import requests
import select
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class DeploySoftware:

    def __init__(self, this):

        self.props = this

        #self.compile()
        self.deploy_software()

    def write_db_config(self, ssh, path):
        cfg = "jdbc.dbtype=mysql\n"

        if int(self.props.rds_num_replicas) > 1:
            cfg += 'jdbc.driverClassName=com.mysql.jdbc.ReplicationDriver\n'
            cfg += 'jdbc.url=jdbc:mysql:replication://%s/%s\n' % (self.props.cfg.get('platform', 'urls'), self.props.database_name )
        else:
            cfg += 'jdbc.driverClassName=com.mysql.jdbc.Driver\n'
            cfg += 'jdbc.url=jdbc:mysql://%s/%s\n' % (self.props.cfg.get('platform', 'urls'), self.props.database_name)

        cfg += 'jdbc.username=%s\n' % self.props.database_user
        cfg += 'jdbc.password=%s\n' % self.props.database_password
        cfg += 'jdbc.hibernate.dialect=org.hibernate.dialect.MySQLDialect\n'

        _, stdout, _ = ssh.exec_command('echo "%s" | sudo tee %s' % (cfg, path))
        self.wait_for_command(stdout)




    # def compile(self):
    #     self.logger.log('Compiling showcase ...')
    #     cmd = 'cd ' + self.file_path + '/showcase;/usr/bin/mvn -Pamazon-hibernate -Dconnection_pool_size=' + self.config.db.get('connection_pool_size') + ' install '
    #     self.logger.log(msg=cmd, level=logging.DEBUG)
    #     #subprocess.check_output(cmd)
    #     ps = subprocess.Popen(cmd, shell=True)
    #     ps.wait()
    #     self.logger.log('Compiled')


    def deploy_software(self):
        for ip_address in self.props.ip_addresses:
            if sys.platform == 'win32':
                self.windows_shell(ip_address);
            else:
                self.unix_shell(ip_address)

    def windows_shell(self,ip_address):
        raise Exception('Not implemented for Windows platform!')

    def unix_shell(self, ip_address):
        try:
            self.props.logger.log("Deploying showcase on " + ip_address)
            self.props.logger.log("This may take a while. Please wait ...")
            time.sleep(60)

            ssh = self.ssh_to_instance(ip_address)

            showcase_url = str(self.props.showcase_location).replace(' ', '%20')
            _, stdout, _ = ssh.exec_command('wget -T90 -q %s -O showcase.war' % showcase_url)
            self.wait_for_command(stdout)

            _, stdout, _ = ssh.exec_command("sudo unzip -o showcase.war -d %s" % self.props.deploy_name)
            self.wait_for_command(stdout)

            self.write_db_config(ssh, 'showcase-1-a/WEB-INF/classes/database/database.aws.hibernate.properties')
            ssh.exec_command("sudo sed -i 's/${connection_pool_size}/%s/g' %s" % (self.props.connection_pool_size, 'showcase-1-a/WEB-INF/classes/hibernate.xml'))

            _, stdout, _ = ssh.exec_command("sudo cp -r showcase-1-a /var/lib/tomcat7/webapps/")
            self.wait_for_command(stdout)

            _, stdout, _ = ssh.exec_command("sudo /etc/init.d/tomcat7 restart")
            self.wait_for_command(stdout)
            time.sleep(60)

            # if not self.check(ip_address):
            #     ssh.exec_command("sudo /etc/init.d/tomcat7 restart")
            ssh.close()
            self.props.logger.log("Successfully finished installation")
        except Exception as e:
            raise e

    def ssh_to_instance(self, ip_address):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        if self.props.key_pair:
            ssh.connect(ip_address, username=self.props.remote_user, key_filename=self.props.key_pair)
        else:
            ssh.connect(ip_address, username=self.props.remote_user)
        return ssh

    def wait_for_command(self, stdout):
         while not stdout.channel.exit_status_ready():
                # Only print data if there is data to read in the channel
                if stdout.channel.recv_ready():
                    rl, wl, xl = select.select([stdout.channel], [], [], 0.0)
                    if len(rl) > 0:
                    # Print data from stdout
                        self.props.logger.log(msg=stdout.channel.recv(1024), level=logging.DEBUG)

if __name__ == '__main__':
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    class Dummy(): pass
    obj = Dummy()
    obj.ip_addresses = cfg.get('infrastructure', 'ip_addresses')
    obj.db_num_instances = cfg.get('RDS', 'num_instances')

    DeploySoftware(obj)
