import novaclient.v2.client as novaclient
import sys
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


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
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    ConfigureMySQL(cfg)