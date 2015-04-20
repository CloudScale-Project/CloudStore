import time

import novaclient.v2 as novaclient

from cloudscale.distributed_jmeter.aws import AWS


class OpenStack(AWS):

    def __init__(self, r_path, scenario_path, output_directory, cfg, logger):
        super(OpenStack, self).__init__(cfg, scenario_path, r_path, output_directory, logger)

    def init(self):
        self.host = self.cfg.get('OPENSTACK', 'host')
        self.startup_threads = self.cfg.get('TEST', 'startup_threads')
        self.rest_threads = self.cfg.get('TEST', 'rest_threads')
        self.host = self.cfg.get('SHOWCASE', 'host')
        self.num_jmeter_slaves = int(self.cfg.get('TEST', 'num_jmeter_slaves'))
        self.key_pair = self.cfg.get('OPENSTACK', 'key_pair_path')
        self.key_name = self.cfg.get('OPENSTACK', 'key_name')
        self.jmeter_url = self.cfg.get('SCENARIO', 'jmeter_url')
        self.user = self.cfg.get('OPENSTACK', 'remote_user')
        self.ips = self.cfg.get('SCENARIO', 'instance_names')
        self.image = self.cfg.get('OPENSTACK', 'image')
        self.flavor = self.cfg.get('OPENSTACK', 'instance_type')
        self.nc = novaclient.Client(
            self.cfg.get('OPENSTACK', 'user'),
            self.cfg.get('OPENSTACK', 'pwd'),
            self.cfg.get('OPENSTACK', 'tenant'),
            auth_url=self.cfg.get('OPENSTACK', 'url')
        )

    def start(self):
        if self.ips != "":
            self.server_ids = [self.nc.servers.find(name=x).id for x in self.ips.split(",")]
        else:
            self.server_ids = [self.create_instance('jmeter-%s' % i) for i in range(self.num_jmeter_slaves) ]

        ips = []
        for server_id in self.server_ids:
            time.sleep(30)
            ip = self.add_floating_ip(server_id)
            ips.append(ip)

        time.sleep(60)
        for ip in ips:
            super(OpenStack, self).setup_master(ip)

        super(OpenStack, self).run_masters(ips)

    def create_instance(self, name):
        self.logger.log("Creating JMeter instance %s" % name)

        image = self.get_image(self.image)
        flavor = self.get_flavor(self.flavor)

        try:
            server = self.nc.servers.create(name,  image, flavor, key_name=self.key_name)
            time.sleep(10)
            self.wait_active(server.id)
        except Exception as e:
            raise e


        for server in self.nc.servers.list():
            if server._info['name'] == name:
                return server.id

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
            time.sleep(10)

    def get_image(self, name):
        for image in self.nc.images.list():
            if image.name == name:
                return image

    def get_flavor(self, name):
        for flavor in self.nc.flavors.list():
            if flavor.name == name:
                return flavor

    def add_floating_ip(self, server_id):

        server = self.nc.servers.get(server_id)
        if len(server._info['addresses']['distributed_jmeter']) > 1:
            return server._info['addresses']['distributed_jmeter'][1]['addr']

        unallocated_floating_ips = self.nc.floating_ips.findall(fixed_ip=None)
        if len(unallocated_floating_ips) < 1:
            unallocated_floating_ips.append(self.nc.floating_ips.create())

        i=0
        floating_ip = unallocated_floating_ips[i]
        i+=1
        while floating_ip.ip == '10.10.43.74' and i < len(unallocated_floating_ips):
            floating_ip = unallocated_floating_ips[i]
        server.add_floating_ip(floating_ip)
        return floating_ip.ip

    def terminate_instances(self, ips):
        #for server_id in self.server_ids:
        #    for server in self.nc.servers.list():
        #        if server.id == server_id:
        #            server.delete()
        return
