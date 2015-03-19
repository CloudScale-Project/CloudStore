import boto.ec2.elb
import boto.rds
import boto.ec2.autoscale
from boto.ec2.autoscale import AutoScalingGroup
from boto.ec2.autoscale import LaunchConfiguration
from boto.ec2.autoscale import ScalingPolicy
import boto.ec2.cloudwatch
from boto.exception import BotoServerError
from boto.ec2.cloudwatch import MetricAlarm
import time
import sys
from cloudscale.deployment_scripts.config import AWSConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class RemoveAll(AWSConfig):
    def __init__(self, cfg, logger):
        AWSConfig.__init__(self, cfg, logger)

        self.conn_ec2 = boto.ec2.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key)

        self.conn_as = boto.ec2.autoscale.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key
        )

        self.conn_cloudwatch = boto.ec2.cloudwatch.connect_to_region(self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )

        self.conn_rds = boto.rds.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key
        )

        conn_elb = boto.ec2.elb.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key)


        self.remove_cloudwatch_alarms()

        self.remove_load_balancer()
        self.remove_ami()
        self.remove_ec2_instances()
        self.remove_autoscale_group()
        self.remove_launch_configuration()
        self.remove_rds_instances()
        # self.remove_security_groups()

        self.logger.log("Done")

    def remove_rds_instances(self):
        self.logger.log("Removing RDS instances ..")
        try:
            self.conn_rds.delete_dbinstance(id='cloudscale-master', skip_final_snapshot=True)
        except BotoServerError as e:
            import traceback
            self.logger.log(traceback.format_exc())
            self.logger.log(e.message)

        num = self.cfg.get('RDS', 'num_replicas')
        for i in xrange(int(num)):
            try:
                self.conn_rds.delete_dbinstance(id='cloudscale-replica%s' % str(i+1), skip_final_snapshot=True)
            except BotoServerError as e:
                import traceback
                self.logger.log(traceback.format_exc())
                self.logger.log(e.message)

    def remove_ec2_instances(self):
        self.logger.log("Terminating EC2 instances ...")

        group = self.conn_as.get_all_groups(['cloudscale-as'])
        instance_ids = []
        if len(group) > 0:
            instance_ids = [i.instance_id for i in group[0].instances]

        if len(instance_ids) > 0:
            self.conn_ec2.terminate_instances(instance_ids=instance_ids)
            for instance_id in instance_ids:
                self.wait_terminate(instance_id)

    def wait_terminate(self, instance_id):
        self.logger.log("Waiting for instance to terminate\nPlease wait . .")
        status = self.conn_ec2.get_all_instances([instance_id])[0].instances[0].state
        i=1
        while status != 'terminated':
            if i%10 == 0:
                self.logger.log("\nPlease wait .")
            self.logger.log(".", append_to_last=True)
            status = self.conn_ec2.get_all_instances([instance_id])[0].instances[0].state
            time.sleep(3)
            i=i+1

        self.logger.log("Instance is terminated!")

    def remove_autoscale_group(self):
        self.logger.log("Removing autoscale group ...")
        try:
            self.conn_as.delete_auto_scaling_group('cloudscale-as')
        except BotoServerError as e:
            import traceback
            self.logger.log(traceback.format_exc())
            self.logger.log(e.message)


    def remove_launch_configuration(self):
        self.logger.log("Removing launch configuration ..")
        try:
            self.conn_as.delete_launch_configuration('cloudscale-lc')
        except BotoServerError as e:
            import traceback
            self.logger.log(traceback.format_exc())
            self.logger.log(e.message)

    def remove_cloudwatch_alarms(self):
        self.logger.log("Removing cloudwatch alarms ...")
        self.conn_cloudwatch.delete_alarms(['scale_up_on_cpu', 'scale_down_on_cpu'])

    def remove_security_groups(self):
        self.logger.log("Removing security groups ...")
        self.conn_ec2.delete_security_group('ssh')
        self.conn_ec2.delete_security_group('http')

    def remove_load_balancer(self):
        self.logger.log("Removing load balancer ...")
        self.conn_elb.delete_load_balancer('cloudscale-lb')

    def remove_ami(self):
        self.logger.log("Removing ami ...")
        try:
            self.conn_ec2.deregister_image(self.cfg.get('infrastructure', 'ami_id'))
        except:
            pass

if __name__ == "__main__":
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])

    RemoveAll(cfg, logger)