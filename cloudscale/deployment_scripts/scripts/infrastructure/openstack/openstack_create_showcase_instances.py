import os
import subprocess
from novaclient.v2 import client as novaclient
import time
import paramiko
import select
from cloudscale.deployment_scripts.config import OpenstackConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger
from cloudscale.deployment_scripts.scripts.software import deploy_showcase


class CreateInstance(OpenstackConfig):

    def __init__(self, config, logger):
        OpenstackConfig.__init__(self, config, logger)
        self.mvn_path = '/usr/bin/mvn'
        self.file_path = "/".join(os.path.abspath(__file__).split('/')[:-1])

        self.remote_deploy_path = self.cfg.get('software', 'remote_deploy_path')

        self.instance_name = 'cloudscale-sc'

        self.showcase_image_name = "cloudscale-sc-image"

        self.showcase_location = self.showcase_url
        self.deploy_name = "showcase-1-a"
        if self.database_type != 'mysql':
            self.deploy_name="showcase-1-b"
            self.showcase_image_name = "cloudscale-sc-mongo-image"

        self.delete_image(self.showcase_image_name)
        self.logger.log("Creating showcase instance image:")
        images = self.nc.images.list()
        for image in images:
            if image.name == self.showcase_image_name:
                self.logger.log('Image already exists.')
                break
        else:
            self.file_path = os.path.dirname(__file__) + "/../../software"

            userdata = """#!/bin/bash
USERNAME=%s
""" % self.remote_user + open(self.file_path + '/install-apache-tomcat.sh', 'r').read()
            server_id = self.create_instance()
            time.sleep(40)
            server_floating_ip = self.add_floating_ip(server_id)
            self.config.save('infrastructure', 'ip_address', server_floating_ip)
            self.ip_addresses = self.cfg.get('infrastructure', 'ip_address').split(",")
            self.add_security_group(server_id, "ssh")

            self.install_software(server_floating_ip, userdata)
            self.upload_configs(server_floating_ip)

            deploy_showcase.DeploySoftware(self)
            #self.deploy_showcase(server_floating_ip)

            self.wait_powered_off(server_floating_ip, server_id)
            self.create_image(server_id, self.showcase_image_name)
            self.delete_instance(server_id)

            self.logger.log('Done creating showcase instance image')

        self.logger.log('Creating showcase instances')
        self.create_showcase_instances()
        self.logger.log('Done creating showcase instances')

    def install_software(self, ip, user_data):
        self.logger.log('Installing apache and tomcat ...')
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        while True:
            try:
                ssh.connect(ip, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                break
            except:
                time.sleep(5)
        time.sleep(30)
        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        scp.put(self.file_path + '/install-apache-tomcat.sh', 'setup.sh')
        _, stdout, _ = ssh.exec_command("sh setup.sh")
        self.wait_for_command(stdout, verbose=True)

    def upload_configs(self, ip):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        while True:
            try:
                ssh.connect(ip, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                break
            except:
                time.sleep(5)

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("Uploading configs on %s" % ip)
        scp.put(self.file_path + '/../platform/server.xml', 'server.xml')
        scp.put(self.file_path + '/cloudscale-apache-virtualhost.conf', 'cloudscale-apache-virtualhost.conf')
        scp.put(self.file_path + '/../platform/mpm_worker.conf', 'mpm_worker.conf')

        _, stdout, _ = ssh.exec_command("sudo mv server.xml /var/lib/tomcat7/conf/server.xml")
        self.wait_for_command(stdout, verbose=True)

        _, stdout, _ = ssh.exec_command("sudo mv cloudscale-apache-virtualhost.conf /etc/apache2/sites-available/cloudscale")
        self.wait_for_command(stdout, verbose=True)

        _, stdout, _ = ssh.exec_command("sudo mv mpm_worker.conf /etc/apache2/mods-enabled/mpm_worker.conf")
        self.wait_for_command(stdout, verbose=True)

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

    def delete_image(self, image_name):
        try:
            for image in self.nc.images.list():
                if image.name == image_name:
                    image.delete()
                    break
        except Exception as e:
            raise e

    def delete_instance(self, server_id):
        self.logger.log("Deleting instance ...")
        server = self.nc.servers.get(server_id)
        server.delete()

    def create_instance(self, image_name=None, files=None, userdata=None, wait_on_active_status=True):
        if image_name is None:
            image_name = self.image_name

        flavor = self.nc.flavors.find(name=self.instance_type)
        if flavor is None:
            self.logger.log("Instance flavor '%s' not found!" % self.instance_type)
            return False

        image = self.nc.images.find(name=image_name)
        if image is None:
            self.logger.log("Image '%s' not found!" % image_name)
            return False

        server_id = self.nc.servers.create(
            self.instance_name, image, flavor, key_name=self.key_name, files=files, userdata=userdata
        ).id

        #if wait_on_active_status and not self.wait_active(server_id):
        #    return False

        return server_id

    def wait_active(self, server_id):
        self.logger.log("Waiting for instance to be built . . .")
        status = self.wait_for_instance_status(server_id, u'BUILD', u'ACTIVE')
        if not status:
            self.logger.log("Can not start instance %s!" % self.instance_name)
            return False
        return True

    def wait_all_instances_active(self, instance_ids):
        for instance_id in instance_ids:
            self.wait_active(instance_id)

    def wait_powered_off(self, ip, server_id):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(ip, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
        _, stdout, _ = ssh.exec_command("sudo poweroff")
        self.wait_for_command(stdout, verbose=True)

        self.logger.log("Waiting for instance %s to be powered off . . ." % server_id)
        status = self.wait_for_instance_status(server_id, u'ACTIVE', u'SHUTOFF')
        if not status:
            self.logger.log("Error on instance %s!" % self.instance_name)
            return False
        return True

    def wait_for_instance_status(self, server_id, current_status, wait_for_status):
        while True:
            server = self.nc.servers.get(server_id)
            if server.status != current_status:
                if server.status == wait_for_status:
                    return True
                return False
            time.sleep(1)

    def create_image(self, server_id, image_name):
        self.logger.log("Creating image ...")
        server = self.nc.servers.get(server_id)
        image_id = server.create_image(image_name)

        while True:
            image = self.nc.images.get(image_id)
            if image.status == u'ACTIVE':
                return True
            if image.status == u'ERROR':
                self.logger.log("Error creating image!")
                return False
            time.sleep(10)

    def compile(self):
        self.logger.log("Downloading showcase from Jenkins...")
        if self.database_type == "mysql":
            ps = subprocess.Popen('cd ' + self.file_path + '/showcase; '+ self.mvn_path + ' -Pamazon-hibernate -Dconnection_pool_size='
                                        + self.connection_pool_size + ' install',
                                  shell=True)
            ps.wait()
            if ps.returncode != 0:
                self.logger.log("ERROR: showcase failed to compile!")
                pass
        else:
            ps = subprocess.Popen('cd ' + self.file_path + '/showcase; ' + self.mvn_path + ' -Pamazon-mongodb -Dconnection_pool_size='
                                        + self.connection_pool_size + ' install',
                                  shell=True)
            ps.wait()
            if ps.returncode != 0:
                self.logger.log("ERROR: showcase failed to compile!")
        pass

    def create_showcase_instances(self):
        showcase_server_ids = []

        for i in range(int(self.num_instances)):
            self.logger.log("Creating showcase instance %s ..." % (i + 1))
            showcase_server_ids.append(
                self.create_instance(image_name=self.showcase_image_name, wait_on_active_status=False)
            )
            time.sleep(60)

        self.wait_all_instances_active(showcase_server_ids)

        for server_id in showcase_server_ids:
            self.add_security_group(server_id, 'http')

    def add_floating_ip(self, server_id):
        server = self.nc.servers.get(server_id)
        unallocated_floating_ips = self.nc.floating_ips.findall(fixed_ip=None)
        if len(unallocated_floating_ips) < 1:
            unallocated_floating_ips.append(self.nc.floating_ips.create())
        floating_ip = unallocated_floating_ips[0]
        server.add_floating_ip(floating_ip)
        return floating_ip.ip

    def add_security_group(self, server_id, group_name):
        self.logger.log("Adding security group %s" % group_name)
        server = self.nc.servers.get(server_id)
        server.add_security_group(group_name)

    def deploy_showcase(self, ip_address):
        self.logger.log("Connecting to ssh on %s" % ip_address)
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        while True:
            try:
                ssh.connect(ip_address, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                break
            except:
                time.sleep(5)

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("Uploading showcase.war")
        ssh.exec_command('wget')
        scp.put(self.file_path + '/showcase/target/showcase-1.0.0-BUILD-SNAPSHOT.war', 'showcase-1-a.war')
        _, stdout, _ = ssh.exec_command("sudo mv showcase-1-a.war /var/lib/tomcat7/webapps")
        self.wait_for_command(stdout, verbose=True)
        ssh.exec_command("sudo chown tomcat7:tomcat7 /var/lib/tomcat7/webapps/showcase-1-a.war")

    def get_ip(self, server):
        for address in server.addresses[server.addresses.keys()[0]]:
            if address['OS-EXT-IPS:type'] == 'fixed':
                server_ip = address['addr']
                break
        else:
            server_ip = None
            self.logger.log("Error: can not get IP address of this server")
        return server_ip

    def create_showcase_database_config(self):
        if self.database_type == 'mysql':
            self.logger.log("Creating jdbc configuration")
            path = self.file_path + '/showcase/src/main/resources/database/database.aws.hibernate.properties'

            master_server = self.nc.servers.findall(name='cloudscale-db-master')
            servers = self.nc.servers.findall(name='cloudscale-db')
            ips = [self.get_ip(master_server[0])]
            for server in servers:
                server_ip = self.get_ip(server)
                ips.append(server_ip)
            urls = ','.join(ips)

            f = open(path, 'w')
            f.write('jdbc.dbtype=mysql\n')
            driver = 'jdbc.driverClassName=com.mysql.jdbc.Driver\n'
            jdbc_url = 'jdbc.url=jdbc:mysql:loadbalance://%s/%s?autoReconnect=true&loadBalanceBlacklistTimeout=5000\n' % (urls, self.database_name)
            if self.config.db.get('setup_type') == 'master-slave':
                driver = 'jdbc.driverClassName=com.mysql.jdbc.ReplicationDriver\n'
                jdbc_url = 'jdbc.url=jdbc:mysql:replication://%s/%s?autoReconnect=true\n' % (urls, self.database_name)

            f.write(driver)
            f.write(jdbc_url)
            f.write('jdbc.username=%s\n' % self.database_user)
            f.write('jdbc.password=%s\n' % self.database_pass)
            f.write('jdbc.hibernate.dialect=org.hibernate.dialect.MySQLDialect\n')
            f.close()
        else:
            self.logger.log("Creating jdbc configuration")
            path = self.file_path + '/showcase/src/main/resources/database/database.aws.mongodb.properties'

            servers = self.nc.servers.findall(name='cloudscale-db-mongo')
            ips = []
            for server in servers:
                server_ip = self.get_ip(server)
                ips.append(server_ip)
            urls = ','.join(ips)

            f = open(path, 'w')
            f.write('jdbc.dbtype=mongodb\n')
            f.write('mongodb.dbname=tpcw\n')
            f.write('mongodb.host=%s\n' % ips[0])
            f.write('mongodb.port=27017\n')
            f.write('mongodb.username=tpcw\n')
            f.write('mongodb.password=Yhm.3Ub+\n')
            f.close()


if __name__ == '__main__':
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    CreateInstance(cfg, logger)
