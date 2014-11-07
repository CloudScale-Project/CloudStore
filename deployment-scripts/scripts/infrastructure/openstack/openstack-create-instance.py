from novaclient.v1_1 import client as novaclient
from common.Cloudscale import *
from scripts.common.Cloudscale import check_args, parse_args


class CreateInstance:

    def __init__(self, cfg):
        self.cfg = cfg
        self.user = cfg.get('OPENSTACK', 'username')
        self.pwd = cfg.get('OPENSTACK', 'password')
        self.url = cfg.get('OPENSTACK', 'auth_url')
        self.tenant = cfg.get('OPENSTACK', 'tenant_name')
        self.image_name = cfg.get('OPENSTACK', 'image_name')
        server = self.create_instance()
        print [s['addr'] for s in server.addresses[self.tenant] if s['OS-EXT-IPS:type'] == 'floating'][0]

    def create_instance(self):
        nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)
        for f in nc.flavors.list():
            print f
        for server in nc.servers.list():
            if server._info['name'] == self.instance_name:
                return server

if __name__ == '__main__':
    check_args(1, "<config_path>")
    _, cfg, _, _ = parse_args()
    CreateInstance(cfg)