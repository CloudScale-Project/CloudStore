import novaclient.v3.client as novaclient
from common.Cloudscale import *

class ConfigureMySQL:

    def __init__(self, cfg):
        self.cfg = cfg
        self.user = cfg.get('OPENSTACK', 'username')
        self.pwd = cfg.get('OPENSTACK', 'password')
        self.url = cfg.get('OPENSTACK', 'auth_url')
        self.tenant = cfg.get('OPENSTACK', 'tenant_name')

        self.create_master()

    def create_master(self):
        pass

if __name__ == '__main__':
    check_args(1, "<config_path>")
    _, cfg, _, _ = parse_args()
    ConfigureMySQL(cfg)