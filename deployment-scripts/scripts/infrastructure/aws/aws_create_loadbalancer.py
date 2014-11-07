from boto import ec2
from boto.exception import BotoServerError


class CreateLoadbalancer:

    def __init__(self, instances, config, logger):
        self.instances = instances
        self.cfg = config.cfg
        self.config = config
        self.logger = logger


    def create(self):
        self.logger.log("Creating load balancer ...")
        conn = ec2.elb.connect_to_region(self.cfg.get('EC2', 'region'),
                                           aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                           aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))

        zones = self.cfg.get('EC2', 'availability_zones').split(",")
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
