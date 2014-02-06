import boto.ec2.elb
import boto.rds
import boto.ec2.autoscale
from boto.ec2.autoscale import AutoScalingGroup
from boto.ec2.autoscale import LaunchConfiguration
from boto.ec2.autoscale import ScalingPolicy
import boto.ec2.cloudwatch
from boto.exception import BotoServerError
from boto.ec2.cloudwatch import MetricAlarm
from common.Cloudscale import *
import time

class RemoveAll:
    def __init__(self, cfg, key_name, key_pair):
        self.cfg = cfg
        self.key_name = key_name
        self.key_pair = key_pair
        self.conn_ec2 = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        self.conn_as = boto.ec2.autoscale.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        self.remove_cloudwatch_alarms()

        self.remove_load_balancer()
        self.remove_ami()
        self.remove_ec2_instances()
        self.remove_autoscale_group()
        self.remove_launch_configuration()
        self.remove_rds_instances()
        # self.remove_security_groups()



        print "Done"

    def remove_rds_instances(self):
        print "Removing RDS instances .."
        conn = boto.rds.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))

        try:
            conn.delete_dbinstance(id='cloudscale-master', skip_final_snapshot=True)
        except BotoServerError as e:
            print e.message

        num = self.cfg.get('RDS', 'num_replicas')
        for i in xrange(int(num)):
            try:
                conn.delete_dbinstance(id='cloudscale-replica%s' % str(i+1), skip_final_snapshot=True)
            except BotoServerError as e:
                print e.message

    def remove_ec2_instances(self):
        print "Terminating EC2 instances ..."

        group = self.conn_as.get_all_groups(['cloudscale-as'])
        instance_ids = []
        if len(group) > 0:
            instance_ids = [i.instance_id for i in group[0].instances]

        if len(instance_ids) > 0:
            self.conn_ec2.terminate_instances(instance_ids=instance_ids)
            for instance_id in instance_ids:
                self.wait_terminate(instance_id)

    def wait_terminate(self, instance_id):
        print "Waiting for instance to terminate\nPlease wait . .",
        status = self.conn_ec2.get_all_instances([instance_id])[0].instances[0].state
        i=1
        while status != 'terminated':
            if i%10 == 0:
                print "\nPlease wait .",
            print ".",
            status = self.conn_ec2.get_all_instances([instance_id])[0].instances[0].state
            time.sleep(3)
            i=i+1

        print "Instance is terminated!"

    def remove_autoscale_group(self):
        print "Removing autoscale group ..."
        try:
            self.conn_as.delete_auto_scaling_group('cloudscale-as')
        except BotoServerError as e:
            print e.message


    def remove_launch_configuration(self):
        print "Removing launch configuration .."
        try:
            self.conn_as.delete_launch_configuration('cloudscale-lc')
        except BotoServerError as e:
            print e.message

    def remove_cloudwatch_alarms(self):
        print "Removing cloudwatch alarms ..."
        conn_cw = boto.ec2.cloudwatch.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        conn_cw.delete_alarms(['scale_up_on_cpu', 'scale_down_on_cpu'])

    def remove_security_groups(self):
        print "Removing security groups ..."
        self.conn_ec2.delete_security_group('ssh')
        self.conn_ec2.delete_security_group('http')

    def remove_load_balancer(self):
        print "Removing load balancer ..."
        conn_elb = boto.ec2.elb.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        conn_elb.delete_load_balancer('cloudscale-lb')

    def remove_ami(self):
        print "Removing ami ..."
        try:
            self.conn_ec2.deregister_image(self.cfg.get('infrastructure', 'ami_id'))
        except:
            pass

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

    RemoveAll(cfg, key_name, key_pair)