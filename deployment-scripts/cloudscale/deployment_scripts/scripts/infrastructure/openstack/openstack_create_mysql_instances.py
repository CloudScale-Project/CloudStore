import os
from novaclient.v2 import client as novaclient
import time
import paramiko
import select
import sys
from cloudscale.deployment_scripts.config import OpenstackConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class ConfigureMySQL(OpenstackConfig):

    def __init__(self, config, logger):
        OpenstackConfig.__init__(config, logger)
        self.master_instance_name = 'cloudscale-db-master'

        self.galera_image_name = "cloudscale-db-image"

        self.logger.log("Creating database instances:")
        self.file_path = os.path.dirname(__file__) + "/../../software"

        if self.mysql_setup_type == 'master-slave':
            self.create_master_slave()
        elif self.mysql_setup_type == 'master-master':
            self.create_master_master()
        else:
            raise Exception("Wrong MYSQL setup type!")


    def create_master_master(self):
        images = self.nc.images.list()
        for image in images:
            if image.name == self.galera_image_name:
                self.logger.log('Image already exists.')
                break
        else:

            userdata = """#!/bin/bash
DB_NAME=%s
DB_USERNAME=%s
DB_PASSWORD=%s
""" % (self.database_name, self.database_user, self.database_pass) + """
USERNAME=%s
""" % self.remote_user + open(self.file_path + '/install-mysql-galera.sh', 'r').read() + """
echo '
""" + open(self.file_path + '/my.cnf', 'r').read().replace("'", "\"'\"") + """
' > /etc/mysql/my.cnf
""" + open(self.file_path + '/install-mysql-galera-import-dump.sh', 'r').read()

            server_id = self.create_instance(userdata=userdata)
            floating_ip = self.add_floating_ip(server_id)
            self.add_security_group(server_id, "ssh")
            self.upload_mysql_dump(floating_ip)

            self.wait_powered_off(server_id)
            self.create_image(server_id, self.galera_image_name)
            self.delete_instance(server_id)

            self.logger.log("Done creating database image")

        self.create_database_instances()

        self.logger.log('Done creating database instances')

    def create_master_slave(self):
        master_ip, master_private_ip = self.create_master()
        slaves = self.create_slave(master_private_ip, int(self.database_num_replicas)-1)
        self.upload_mysql_dump(master_ip)
        ssh = self.ssh_to_instance(master_ip)
        _ ,stdout, _ = ssh.exec_command("mysql -u root -ppassword -D tpcw < ~/dump.sql")
        self.wait_for_command(stdout, True)
        self.config.save('platform', 'urls', master_private_ip + ',' + ",".join([private_ip for _, private_ip in slaves]) )

    def create_master(self):
        tmp_instance_name = self.instance_name
        self.instance_name = self.master_instance_name

        server_id = self.create_instance(image_name=self.image_name)
        time.sleep(40)
        self.instance_name = tmp_instance_name

        floating_ip = self.add_floating_ip(server_id)
        self.add_security_group(server_id, "mysql")
        ssh = self.ssh_to_instance(floating_ip);

        self.scp_file(self.file_path+"/master_slave/my.cnf.master", "my.cnf", ssh)
        self.scp_file(self.file_path+"/master_slave/setup_master.sh", "setup_master.sh", ssh)
        _, stdout, _ = ssh.exec_command("sudo mv ~/my.cnf /etc/mysql/my.cnf; sh ~/setup_master.sh")
        #self.wait_for_command(stdout, True)
        foo = stdout.readlines()
        if len(foo) > 0:
            self.master_file, self.master_position = foo[2].split("|")

            ssh.close()
            private_ip = self.nc.servers.get(server_id)._info.get('addresses').get(self.tenant)[0].get('addr')
            return floating_ip, private_ip
        raise Exception("Can't get master_file and master_position")

    def create_slave(self, master_ip, num_replicas):
        slaves = [self.create_slave_instance(image_name=self.image_name) for _ in xrange(num_replicas)]
        i = 2
        for ip, _ in slaves:
            ssh = self.ssh_to_instance(ip)
            self.scp_file(self.file_path+"/master_slave/my.cnf.slave", "my.cnf", ssh)
            _, stdout,_ = ssh.exec_command("sudo mv ~/my.cnf /etc/mysql/my.cnf; sudo sed -i 's/^#server-id\s\+= 1$/server-id = %s/g' /etc/mysql/my.cnf" % i)
            self.wait_for_command(stdout, True)
            _, stdout, _ = ssh.exec_command("""sudo service mysql restart; mysql -u root -ppassword -e "STOP SLAVE;" """)
            self.wait_for_command(stdout, True)
            cmd = '''mysql -u root -ppassword -e "CHANGE MASTER TO MASTER_HOST='%s',MASTER_USER='slave_user', MASTER_PASSWORD='password', MASTER_LOG_FILE='%s', MASTER_LOG_POS=  %s;"''' % (master_ip, self.master_file, self.master_position)
            f = open("tmp.sh", "w")
            f.write(cmd)
            f.close()
            self.scp_file("tmp.sh", "tmp.sh", ssh)
            _, stdout, _ = ssh.exec_command("sh tmp.sh")
            self.wait_for_command(stdout, True)
            cmd = "mysql -u root -ppassword -e \"CREATE DATABASE tpcw\""
            _, stdout, _ = ssh.exec_command(cmd)
            self.wait_for_command(stdout, True)

            cmd = "mysql -u root -ppassword -e \"GRANT ALL ON tpcw.* TO 'root'@'%' IDENTIFIED BY 'password'\""

            _, stdout, _ = ssh.exec_command(cmd)
            self.wait_for_command(stdout, True)

            _, stdout, _ = ssh.exec_command("mysql -u root -ppassword -e \"START SLAVE;\"")
            ssh.close()
            i+=1
        return slaves

    def create_slave_instance(self, image_name=None):
        server_id = self.create_instance(image_name=image_name)
        time.sleep(10)
        floating_ip = self.add_floating_ip(server_id)
        self.add_security_group(server_id, "mysql")
        private_ip = self.get_private_ip(server_id)
        return floating_ip, private_ip

    def get_private_ip(self,server_id, i=0):
        try:
            return self.nc.servers.get(server_id)._info.get('addresses').get(self.tenant)[0].get('addr')
        except Exception as e:
            if i<3:
                return self.get_private_ip(server_id, i+1)
            else:
                raise e

    def scp_file(self, file, remote_file, ssh):
        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        scp.put(file, remote_file)

    def delete_instance(self, server_id):
        self.logger.log("Deleting instance ...")
        server = self.nc.servers.get(server_id)
        server.delete()

    def create_instance(self, image_name=None, userdata=None, wait_on_active_status=True):
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

        net = self.nc.networks.find(label=self.tenant)
        nics = [{'net-id' : net.id}]

        server_id = self.nc.servers.create(self.instance_name, image, flavor, key_name=self.key_name, userdata=userdata, nics=nics).id

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

    def wait_powered_off(self, server_id):
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
            time.sleep(3)

    def create_database_instances(self):
        self.logger.log("Creating first instance ...")

        database_server_ids = []

        userdata = open(self.file_path + '/start-mysql-galera-first-node.sh', 'r').read()

        server_id = self.create_instance(image_name=self.galera_image_name, userdata=userdata)
        database_server_ids.append(server_id)
        time.sleep(10)
        server = self.nc.servers.get(server_id)
        for address in server.addresses[server.addresses.keys()[0]]:
            if address['OS-EXT-IPS:type'] == 'fixed':
                server_ip = address['addr']
                break
        else:
            server_ip = None
            self.logger.log("Error: can not get IP address of first node")
        self.logger.log("IP address of first node: %s" % server_ip)

        userdata = """#!/bin/bash
FIRST_NODE_IP=%s
""" % server_ip + open(self.file_path + '/start-mysql-galera-other-nodes.sh', 'r').read()

        for i in range(int(self.num_replicas) - 1):
            self.logger.log("Creating database instance %s ..." % (i + 2))
            database_server_ids.append(
                self.create_instance(image_name=self.galera_image_name, userdata=userdata, wait_on_active_status=False)
            )

        self.wait_all_instances_active(database_server_ids)

        for server_id in database_server_ids:
            self.add_security_group(server_id, 'galera')

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


    def ssh_to_instance(self, ip_addr):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        while True:
            try:
                ssh.connect(ip_addr, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                break
            except:
                time.sleep(5)
        return ssh

    def upload_mysql_dump(self, ip_address):
        self.logger.log("Connecting to ssh on %s" % ip_address)
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        while True:
            try:
                ssh.connect(ip_address, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                break
            except:
                time.sleep(5)

        #scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("Uploading mysql dump")
        #scp.put(self.generate_dump_path, 'dump.sql')
        ssh.exec_command("wget -q -T90 http://cloudscale.xlab.si/github/rds-tpcw-dump-latest.sql -O dump.sql")

        ssh.exec_command("touch finished")


if __name__ == '__main__':
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    ConfigureMySQL(cfg, logger)
