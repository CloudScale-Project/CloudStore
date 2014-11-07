import base64
from novaclient.v1_1 import client as novaclient
import time
from scripts.common.Cloudscale import *
from webservice.settings import BASE_DIR


class CreateInstance:

    def __init__(self, config, logger):
        self.cfg = config.cfg
        self.config = config
        self.logger = logger

        self.user = self.cfg.get('OPENSTACK', 'username')
        self.pwd = self.cfg.get('OPENSTACK', 'password')
        self.url = self.cfg.get('OPENSTACK', 'auth_url')
        self.tenant = self.cfg.get('OPENSTACK', 'tenant_name')

        self.image_name = self.cfg.get('OPENSTACK', 'image_name')

        self.instance_type = self.cfg.get('OPENSTACK', 'instance_type')
        self.instance_name = 'cloudscale-lb'
        self.num_instances = self.config.fr.get('num_instances')
        self.key_name = self.cfg.get('OPENSTACK', 'key_name')
        self.key_pair = self.cfg.get('OPENSTACK', 'key_pair')

        self.database_type = self.cfg.get('OPENSTACK', 'database_type').lower()

        self.nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)

        showcase_servers = self.nc.servers.findall(name="cloudscale-sc")

        if len(showcase_servers) == 1:
            self.logger.log("Only one instance")
            server_id = showcase_servers[0].id
            self.load_balancer_public_ip = self.add_floating_ip(server_id)
            self.add_security_group(server_id, "ssh")
            return

        self.logger.log("Creating load balancer instance:")

        self.file_path = BASE_DIR + "/scripts/software"

        ha_proxy_config = open(self.file_path + '/haproxy.cfg', 'r').read()
        checker = base64.b64encode(open(self.file_path + '/check-running-showcase_instances.py', 'r').read())
        openstack_config = base64.b64encode(open(self.config.config_path, 'r').read())

        #############################################################################################################
        # TODO: remove this when instances can connect to openstack, as servers will be automatically added by script
        # add showcase servers to config
        for server in showcase_servers:
            for address in server.addresses[server.addresses.keys()[0]]:
                if address['OS-EXT-IPS:type'] == 'fixed':
                    server_ip = address['addr']
                    break
            else:
                server_ip = None
                self.logger.log("Error: can not get IP address")
            ha_proxy_config += """
    server %s %s:80 check""" % (server_ip, server_ip)
        #############################################################################################################

        userdata = open(self.file_path + '/install-load-balancer.sh', 'r').read()
        userdata = ha_proxy_config.join(
            userdata.split("#####REPLACE_ME_WITH_CONFIG#####")
        )
        userdata = checker.join(
            userdata.split("###PLACEHOLDER_FOR_checker.py###")
        )
        userdata = openstack_config.join(
            userdata.split("###PLACEHOLDER_FOR_config.ini###")
        )

        server_id = self.create_instance(userdata=userdata)

        self.add_security_group(server_id, "ssh")
        self.add_security_group(server_id, "http")

        self.load_balancer_public_ip = self.add_floating_ip(server_id)

        self.logger.log('Done creating load balancer instance')

    def get_public_ip(self):
        return self.load_balancer_public_ip

    def create_instance(self, image_name=None, files=None, userdata=None, wait_on_active_status=True):
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

        server_id = self.nc.servers.create(
            self.instance_name, image, flavor, key_name=self.key_name, files=files, userdata=userdata
        ).id

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


if __name__ == '__main__':
    check_args(1, "<config_path>")
    config_file, cfg, key_name, key_pair = parse_args('OPENSTACK')
    CreateInstance(config_file, cfg, key_name, key_pair)
