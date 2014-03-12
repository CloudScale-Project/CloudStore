from common.Cloudscale import check_args
from common.Cloudscale import parse_args
import boto.ec2.elb

class CreateLoadBalancer:

    def __init__(self, cfg, key_name, key_pair, config_path):
        self.cfg = cfg
        self.key_pair = key_pair
        self.key_name = key_name
        lb = self.create_load_balancer()
        self.cfg.save_option(config_path, 'infrastructure', 'loadbalancer_name', lb.name)
        self.cfg.save_option(config_path, 'infrastructure', 'loadbalancer_url', lb.dns_name)

    def create_load_balancer(self):
        print "Creating load balancer ..."
        conn = boto.ec2.elb.connect_to_region(self.cfg.get('EC2', 'region'),
                                              aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                              aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))

        zones = self.cfg.get('EC2', 'availability_zones').split(",")
        ports = [(80, 80, 'http')]

        lb = conn.create_load_balancer('cloudscale-lb', zones, ports)
        return lb

if __name__ == "__main__":
    check_args(1, "<config_path>")
    config_path, cfg, key_name, key_pair = parse_args()

    CreateLoadBalancer(cfg, key_name, key_pair, config_path)