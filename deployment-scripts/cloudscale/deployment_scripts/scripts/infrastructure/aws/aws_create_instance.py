import boto, boto.exception
import boto, boto.ec2
import boto.manage.cmdshell
import time
import paramiko

import sys, os
from cloudscale.deployment_scripts.config import AWSConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class CreateEC2Instance(AWSConfig):

    def __init__(self, config, logger):
        AWSConfig.__init__(self, config, logger)

        self.conn = boto.ec2.connect_to_region(
            self.region,
            aws_access_key_id=self.access_key,
            aws_secret_access_key=self.secret_key
        )


    def create(self):
        self.create_security_groups()
        instance = self.create_instance()

        return instance

    def create_all(self, num_instances):
        res = self.conn.run_instances(
            self.ami_id,
            max_count=num_instances,
            key_name=self.key_name,
            instance_type=self.instance_type,
            security_groups=['http', 'ssh'],
            monitoring_enabled=True,
            placement=self.availability_zone
        )

        instance_ids = []
        for instance in res.instances:
            self.wait_available(instance)
            instance_ids.append(instance.id)

        instances = self.conn.get_all_instances(instance_ids)[0].instances
        self.conn.create_tags(instance_ids, {'Name': self.instance_identifier})

        return instances

    def create_security_groups(self):
        self.logger.log("Creating security groups http and ssh ...")
        self.create_security_group('http', 'Security group for HTTP protocol', '80', '0.0.0.0/0')
        self.create_security_group('ssh', 'Security group for HTTP protocol', '22', '0.0.0.0/0')

    def create_security_group(self, name, description, port, cidr):
        try:
            self.conn.create_security_group(name, description)
            self.conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise

    def create_instance(self):
        self.logger.log("Creating EC2 instance ...")
        res = self.conn.run_instances(
            self.ami_id,
            key_name=self.key_name,
            instance_type=self.instance_type,
            security_groups=['http', 'ssh'],
            monitoring_enabled=True,
            placement=self.availability_zone
        )
        self.wait_available(res.instances[0])

        instance = self.conn.get_all_instances([res.instances[0].id])[0].instances[0]
        self.conn.create_tags([instance.id], {'Name': self.instance_identifier})
        self.conn.monitor_instances([instance.id])
        return instance

    def wait_available(self, instance):
        self.logger.log("Waiting for instance to become available\nPlease wait . .")
        status = self.conn.get_all_instances([instance.id])[0].instances[0].state
        i=1
        while status != 'running':
            if i%10 == 0:
                self.logger.log("\nPlease wait .")
            self.logger.log(".", append_to_last=True)
            status = self.conn.get_all_instances([instance.id])[0].instances[0].state
            time.sleep(3)
            i=i+1

        self.logger.log("Instance is running!")

if __name__ == '__main__':
    check_args(2, "<config_path>")
    user_path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    CreateEC2Instance(cfg, logger)