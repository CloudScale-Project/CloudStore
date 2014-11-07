# from common.Cloudscale import *
import boto.ec2
import time
from scripts.common.Cloudscale import check_args, parse_args


class EC2CreateAMI:

    def __init__(self, config, logger):
        self.logger = logger
        self.cfg = config.cfg
        self.config = config
        self.key_name = self.cfg.get('EC2', 'key_name')
        self.key_pair = self.cfg.get('EC2', 'key_pair')
        self.conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))

        ami_id = self.create_ami(self.cfg.get('infrastructure', 'ip_address'))
        self.config.save('infrastructure', 'ami_id', ami_id)
        self.logger.log("Done")

    def create_ami(self, instance_ip):
        self.logger.log("Creating AMI from instance %s" % instance_ip)
        if instance_ip is None:
            self.logger.log("instance_ip is null")
            exit(0)

        instance_id = None
        for instance in self.conn.get_only_instances():
            if instance.ip_address == instance_ip:
                instance_id = instance.id
                break

        if instance_id is None:
            self.logger.log("Can't find any instances to create ami from!")
            exit(0)
        try:
            image_id = self.conn.create_image(instance_id, 'cloudscale-as-image')
            self.wait_available(image_id)
            self.terminate_instance(instance_id)
            return image_id
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) == 'InvalidAMIName.Duplicate':
                image = self.conn.get_all_images(filters={'name' : 'cloudscale-as-image'})[0]
                image.deregister()
                return self.create_ami(instance_ip)
        self.logger.log("Error creating AMI image")
        exit(0)

    def terminate_instance(self, instance_id):
        self.conn.terminate_instances([instance_id])
        self.wait_terminate(instance_id)

    def wait_available(self, image_id):
        self.logger.log("Waiting to create AMI from instance ..")
        status = self.conn.get_all_images(image_ids=[image_id])[0].state
        i=1
        while status != 'available':
            if i%10 == 0:
                self.logger.log("\nPlease wait .")
            self.logger.log(".", append_to_last=True)
            status = self.conn.get_all_images(image_ids=[image_id])[0].state
            time.sleep(3)
            i=i+1

        self.logger.log("Done")

    def wait_terminate(self, instance_id):
        self.logger.log("Waiting for instance to terminate\nPlease wait ..")
        status = self.conn.get_all_instances([instance_id])[0].instances[0].state
        i=1
        while status != 'terminated':
            if i%10 == 0:
                self.logger.log("\nPlease wait .")
            self.logger.log(".", append_to_last=True)
            status = self.conn.get_all_instances([instance_id])[0].instances[0].state
            time.sleep(3)
            i=i+1

        self.logger.log("Instance is terminated!")

if __name__ == "__main__":
    check_args(1, "<config_path>")
    config_file, cfg, key_name, key_pair = parse_args()
    EC2CreateAMI(config_file, cfg, key_name, key_pair)


