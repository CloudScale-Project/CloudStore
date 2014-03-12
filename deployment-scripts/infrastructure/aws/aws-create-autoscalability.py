import boto.ec2.elb
import boto.ec2.autoscale
from boto.ec2.autoscale import AutoScalingGroup
from boto.ec2.autoscale import LaunchConfiguration
from boto.ec2.autoscale import ScalingPolicy
import boto.ec2.cloudwatch
from boto.ec2.cloudwatch import MetricAlarm
from common.Cloudscale import *

class Autoscalability:
    def __init__(self, cfg, key_name, key_pair):
        self.cfg = cfg
        self.key_name = key_name
        self.key_pair = key_pair
        self.conn = boto.ec2.autoscale.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        lb_name = self.create_load_balancer()
        self.create_security_group('http', 'Security group for HTTP', '0.0.0.0/0', '80')
        self.create_security_group('ssh', 'Security group for SSH', '0.0.0.0/0', '22')

        lc = self.create_launch_configuration()
        self.create_autoscalability_group(lb_name, lc)
        scale_up_policy_arn, scale_down_policy_arn = self.create_scaling_policy()
        self.create_cloudwatch_alarms(scale_up_policy_arn, scale_down_policy_arn)

        print "Done"

    def create_cloudwatch_alarms(self, scale_up_policy_arn, scale_down_policy_arn):
        print "Creating CloudWatch alarms"

        conn = boto.ec2.cloudwatch.connect_to_region(self.cfg.get('EC2', 'region'),
                                                     aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                                     aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        alarm_dimensions = {'AutoScalingGroupName' : 'cloudscale-as'}

        scale_up_alarm = MetricAlarm(
            name='scale_up_on_cpu', namespace='AWS/EC2',
            metric='CPUUtilization', statistic='Average',
            comparison='>', threshold='70',
            period='60', evaluation_periods=2,
            alarm_actions=[scale_up_policy_arn],
            dimensions=alarm_dimensions)

        scale_down_alarm = MetricAlarm(
            name='scale_down_on_cpu', namespace='AWS/EC2',
            metric='CPUUtilization', statistic='Average',
            comparison='<', threshold='40',
            period='60', evaluation_periods=2,
            alarm_actions=[scale_down_policy_arn],
            dimensions=alarm_dimensions)

        conn.create_alarm(scale_up_alarm)
        conn.create_alarm(scale_down_alarm)

    def create_scaling_policy(self):
        print "Creating scaling policy ..."
        scale_up_policy = ScalingPolicy(name='scale_up',
                                        adjustment_type='ChangeInCapacity',
                                        as_name='cloudscale-as',
                                        scaling_adjustment=2,
                                        cooldown=180
                        )
        scale_down_policy = ScalingPolicy(name='scale_down',
                                          adjustment_type='ChangeInCapacity',
                                          as_name='cloudscale-as',
                                          scaling_adjustment=-2,
                                          cooldown=180)
        self.conn.create_scaling_policy(scale_up_policy)
        self.conn.create_scaling_policy(scale_down_policy)
        scale_up_policy = self.conn.get_all_policies(
            as_group='cloudscale-as', policy_names=['scale_up'])[0]
        scale_down_policy = self.conn.get_all_policies(
            as_group='cloudscale-as', policy_names=['scale_down'])[0]

        return scale_up_policy.policy_arn, scale_down_policy.policy_arn

    def create_launch_configuration(self):
        print "Creating launch configuration ..."

        try:
            lc = LaunchConfiguration(self.conn,
                                 "cloudscale-lc",
                                 self.cfg.get('infrastructure', 'ami_id'),
                                 self.key_name,
                                 ['http'],
                                 None,
                                 self.cfg.get('EC2', 'instance_type'))

            self.conn.create_launch_configuration(lc)
            return lc
        except boto.exception.BotoServerError as e:
            if e.error_code == 'AlreadyExists':
                return self.conn.get_all_launch_configurations(names=['cloudscale-lc'])
            else:
                raise

    def create_autoscalability_group(self, lb_name, lc):
        print "Creating autoscalability group ..."

        try:
            ag = AutoScalingGroup(group_name='cloudscale-as',
                              load_balancers=[lb_name],
                              availability_zones=self.cfg.get('EC2', 'availability_zones').split(","),
                              launch_config=lc, min_size=2, max_size=8, connection=self.conn)
            self.conn.create_auto_scaling_group(ag)
        except boto.exception.BotoServerError as e:
            if e.error_code != 'AlreadyExists':
                raise # self.conn.get_all_groups(names=['cloudscale-as'])[0]


    def create_security_group(self, name, description, cidr, port):
        try:
            conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
            conn.create_security_group(name, description)
            conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)

            conn.create_dbsecurity_group(name, description)
            conn.authorize_dbsecurity_group(name, cidr, name)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise

    def create_load_balancer(self):
        print "Creating load balancer ..."
        conn = boto.ec2.elb.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))

        zones = self.cfg.get('EC2', 'availability_zones').split(",")
        ports = [(80, 80, 'http')]

        lb = conn.create_load_balancer('cloudscale-lb', zones, ports)

        return lb.name


if __name__ == "__main__":
    check_args(1, "<config_path>")
    _, cfg, key_name, key_pair = parse_args()

    Autoscalability(cfg, key_name, key_pair)