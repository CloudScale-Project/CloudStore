from novaclient.v2 import client as novaclient
import sys
from cloudscale.deployment_scripts.config import OpenstackConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class CreateInstance(OpenstackConfig):

    def __init__(self, config, logger):
        OpenstackConfig.__init__(self, config, logger)

        server = self.create_instance()
        print [s['addr'] for s in server.addresses[self.tenant] if s['OS-EXT-IPS:type'] == 'floating'][0]

    def create_instance(self):
        nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)
        for f in nc.flavors.list():
            print f
        # nc.servers.create('cloudscale', self.image_name, )
        for server in nc.servers.list():
            if server._info['name'] == self.instance_name:
                return server

if __name__ == '__main__':
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    CreateInstance(cfg, logger)