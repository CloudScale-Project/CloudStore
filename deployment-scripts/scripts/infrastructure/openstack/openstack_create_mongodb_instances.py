import os
from novaclient.v1_1 import client as novaclient
import time
import paramiko
from scripts.common.Cloudscale import check_args, parse_args
from webservice.settings import BASE_DIR


class ConfigureMongodb:

    def __init__(self, config, logger):
        self.cfg = config.cfg
        self.config = config
        self.logger = logger

        self.user = self.cfg.get('OPENSTACK', 'username')
        self.pwd = self.cfg.get('OPENSTACK', 'password')
        self.url = self.cfg.get('OPENSTACK', 'auth_url')
        self.tenant = self.cfg.get('OPENSTACK', 'tenant_name')

        self.image_name = self.cfg.get('OPENSTACK', 'image_name')

        self.instance_type = self.cfg.get('MONGODB', 'instance_type')
        self.instance_name = 'cloudscale-db-mongo'
        self.num_replicas = self.cfg.get('MONGODB', 'num_replicas')

        self.key_name = self.cfg.get('OPENSTACK', 'key_name')
        self.key_pair = self.cfg.get('OPENSTACK', 'key_pair')

        self.mongodb_image_name = "cloudscale-db-mongo-image"

        self.database_name = self.cfg.get('MONGODB', 'database_name')
        self.database_user = self.cfg.get('MONGODB', 'database_user')
        self.database_pass = self.cfg.get('MONGODB', 'database_pass')

        self.generate_dump_path = BASE_DIR + '/scripts/' + self.cfg.get('MONGODB', 'generate_dump_path')

        self.database_type = self.cfg.get('OPENSTACK', 'database_type').lower()

        self.remote_user = self.cfg.get('OPENSTACK', 'image_username')

        self.nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)

        self.logger.log("Creating database instances:")
        images = self.nc.images.list()
        for image in images:
            if image.name == self.mongodb_image_name:
                self.logger.log('Image already exists.')
                break
        else:
            self.file_path = BASE_DIR + "/scripts/software"

            userdata = """#!/bin/bash
USERNAME=%s
""" % self.remote_user + open(self.file_path + '/install-mongodb.sh', 'r').read()

            server_id = self.create_instance(userdata=userdata)
            self.wait_powered_off(server_id)
            self.create_image(server_id, self.mongodb_image_name)
            self.delete_instance(server_id)

            self.logger.log("Done creating database image")

        database_server_ids = self.create_database_instances()

        self.start_mongo_on_instances(database_server_ids)

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

    def remove_floating_ip(self, server_id, floating_ip):
        server = self.nc.servers.get(server_id)
        server.remove_floating_ip(floating_ip)

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
        database_server_ids = []

        for i in range(int(self.num_replicas)):
            self.logger.log("Creating database instance %s ..." % (i + 1))
            database_server_ids.append(
                self.create_instance(image_name=self.mongodb_image_name, wait_on_active_status=False)
            )

        self.wait_all_instances_active(database_server_ids)
        return database_server_ids

    def upload_mongo_dump(self, ip_address):
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

        ssh.exec_command("sudo mv dump.sql /root/dump.sql")

    def get_ip(self, server_id):
        server = self.nc.servers.get(server_id)

        for address in server.addresses[server.addresses.keys()[0]]:
            if address['OS-EXT-IPS:type'] == 'fixed':
                server_ip = address['addr']
                break
        else:
            server_ip = None
            self.logger.log("Error: can not get IP address of this server")
        return server_ip

    def ssh_execute_command(self, ssh, command):
        transport = ssh.get_transport()
        channel = transport.open_session()
        channel.exec_command(command)
        return channel.recv_exit_status()

    def start_mongo_on_instances(self, database_server_ids):
        self.logger.log("Creating ssh connections")
        ssh_connections = []  # (server_id, ip_address, ssh, floating_ip)
        for server_id in database_server_ids:
            self.add_security_group(server_id, 'mongo')
            self.add_security_group(server_id, 'ssh')
            floating_ip = self.add_floating_ip(server_id)

            self.logger.log("Connecting to ssh on %s" % floating_ip)
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            while True:
                try:
                    ssh.connect(floating_ip, username=self.remote_user, key_filename=os.path.abspath(self.key_pair))
                    break
                except:
                    time.sleep(5)

            ssh_connections.append((server_id, self.get_ip(server_id), ssh, floating_ip))

        config_servers = []

        self.logger.log("Starting config servers")
        num_config_servers = 3 if len(ssh_connections) >= 3 else 1
        for i in range(num_config_servers):
            config_servers.append(ssh_connections[i][1] + ":27019")
            ssh = ssh_connections[i][2]
            self.ssh_execute_command(
                ssh,
                """sudo mkdir -p /data/configdb
sudo mongod --configsvr --dbpath /data/configdb --port 27019 --syslog --fork""")

        config_servers = ','.join(config_servers)

        self.logger.log("Starting mongos")
        for i in range(len(ssh_connections)):
            ssh = ssh_connections[i][2]
            self.ssh_execute_command(ssh, "sudo mongos --configdb %s --syslog --fork" % config_servers)

        self.logger.log("Adding shards")
        for i in range(len(ssh_connections)):
            ssh = ssh_connections[i][2]
            fixed_ip = ssh_connections[i][1]
            self.ssh_execute_command(ssh, """echo 'sh.addShard("%s:27037")' | mongo""" % fixed_ip)

        # only on one server
        ssh = ssh_connections[0][2]

        self.logger.log("Configuring sharding for database and collections")
        self.ssh_execute_command(ssh, """echo 'sh.enableSharding("%s")' | mongo""" % self.database_name)
        collections = ['address', 'author', 'ccxacts', 'customer', 'item', 'orderLine',
                       'orderLineMR', 'orders', 'shoppingCart', 'shoppingCartLine']
        default_shard_key_pattern = '{ "_id": "hashed" }'
        for collection in collections:
            shard_key_pattern = default_shard_key_pattern
            self.ssh_execute_command(ssh, """echo 'sh.shardCollection("%s.%s", %s )' | mongo"""
                                          % (self.database_name, collection, shard_key_pattern))

        scp = paramiko.SFTPClient.from_transport(ssh.get_transport())
        self.logger.log("Uploading mongo dump")
        scp.put(self.generate_dump_path, 'dump.tar.gz')

        self.logger.log("Extracting mongo dump")
        self.ssh_execute_command(ssh, "mkdir dump; tar xf dump.tar.gz -C dump")

        self.logger.log("Importing mongo dump")
        self.ssh_execute_command(ssh, "mongorestore --db tpcw dump/tpcw/")

        self.logger.log("Removing dump.tar.gz and folder dump/")
        self.ssh_execute_command(ssh, "rm dump.tar.gz; rm -r dump")

        self.logger.log("Creating user")
        self.ssh_execute_command(ssh, """echo 'db.createUser({
    "user": "%s",
    "pwd": "%s",
    "roles": [ { "role": "readWrite", "db": "%s" } ]
})' | mongo %s""" % (self.database_user, self.database_pass, self.database_name, self.database_name))

        self.logger.log("Close ssh connections, and removing public IPs and ssh security groups")
        for i in range(len(ssh_connections)):
            server_id = ssh_connections[i][0]
            fixed_ip = ssh_connections[i][1]
            ssh = ssh_connections[i][2]
            floating_ip = ssh_connections[i][3]
            ssh.close()
            self.remove_floating_ip(server_id, floating_ip)

