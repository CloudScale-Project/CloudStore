from boto import ec2
from boto.exception import BotoServerError
import sys
import time
from cloudscale.deployment_scripts.config import AWSConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class CreateLoadbalancer(AWSConfig):

    def __init__(self, config, logger):
        AWSConfig.__init__(self, config, logger)

    def create(self, instances):
        self.instances = instances
        self.logger.log("Creating load balancer ...")
        conn = ec2.elb.connect_to_region(self.region,
                                           aws_access_key_id=self.access_key,
                                           aws_secret_access_key=self.secret_key)

        zones = [self.availability_zone]
        ports = [(80, 80, 'http')]

        lb_name = 'cloudscale-lb'
        try:
            lb = conn.get_all_load_balancers(load_balancer_names=[lb_name])
        except BotoServerError as e:
            lb =[]

        i = 2
        while len(lb) > 0:
            lb_name = 'cloudscale-lb-%s' % i
            try:
                lb = conn.get_all_load_balancers(load_balancer_names=[lb_name])
            except BotoServerError as e:
                lb = []
            i+=1

        lb = conn.create_load_balancer(lb_name, zones, ports)

        self.attach_instances(lb)

        return lb

    def attach_instances(self, lb):
        lb.register_instances([i.id for i in self.instances])

if __name__ == '__main__':
    check_args(2, "<config_path>")
    user_path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    CreateLoadbalancer(cfg, logger)