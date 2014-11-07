import os
from novaclient.v1_1 import client as novaclient
import time
import paramiko
from scripts.common.Cloudscale import check_args, parse_args
from webservice.settings import BASE_DIR


class ConfigureMySQL:

    def __init__(self, config, logger):
        self.cfg = config.cfg
        self.config = config
        self.logger = logger

        self.user = self.cfg.get('OPENSTACK', 'username')
        self.pwd = self.cfg.get('OPENSTACK', 'password')
        self.url = self.cfg.get('OPENSTACK', 'auth_url')
        self.tenant = self.cfg.get('OPENSTACK', 'tenant_name')

        self.image_name = self.cfg.get('OPENSTACK', 'image_name')

        self.instance_type = self.cfg.get('MYSQL', 'instance_type')
        self.instance_name = 'cloudscale-db'
        self.num_replicas = self.cfg.get('MYSQL', 'num_replicas')

        self.key_name = self.cfg.get('OPENSTACK', 'key_name')
        self.key_pair = self.cfg.get('OPENSTACK', 'key_pair')

        self.galera_image_name = "cloudscale-db-image"

        self.database_name = self.cfg.get('MYSQL', 'database_name')
        self.database_user = self.cfg.get('MYSQL', 'database_user')
        self.database_pass = self.cfg.get('MYSQL', 'database_pass')

        self.generate_dump_path = BASE_DIR + '/scripts/' + self.cfg.get('MYSQL', 'generate_dump_path')

        self.database_type = self.cfg.get('OPENSTACK', 'database_type').lower()

        self.remote_user = self.cfg.get('OPENSTACK', 'image_username')

        self.nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)

        self.logger.log("Creating database instances:")
        images = self.nc.images.list()
        self.file_path = BASE_DIR + "/scripts/software"
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

    def delete_instance(self, server_id):
        self.logger.log("Deleting instance ...")
        server = self.nc.servers.get(server_id)
        server.delete()

    def create_instance(self, image_name=None, userdata=None, wait_on_active_status=True):
        if image_name is None:
            image_name = self.image_name

        for f in self.nc.flavors.list():
            if f.name == self.instance_type:
                flavor = f
                break
        else:
            self.logger.log("Instance flavor '%s' not found!" % self.instance_type)
            return False

        for img in self.nc.images.list():
            if img.name == image_name:
                image = img
                break
        else:
            self.logger.log("Image '%s' not found!" % image_name)
            return False

        server_id = self.nc.servers.create(self.instance_name, image, flavor, key_name=self.key_name, userdata=userdata).id

        if wait_on_active_status and not self.wait_active(server_id):
            return False

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

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("Uploading mysql dump")
        scp.put(self.generate_dump_path, 'dump.sql')

        ssh.exec_command("touch finished")


if __name__ == '__main__':
    check_args(1, "<config_path>")
    _, cfg, key_name, key_pair = parse_args('OPENSTACK')
    ConfigureMySQL(cfg, key_name, key_pair)
