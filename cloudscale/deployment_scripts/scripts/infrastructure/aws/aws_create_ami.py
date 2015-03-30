# from common.Cloudscale import *
import boto.ec2
import time
import sys
from cloudscale.deployment_scripts.config import AWSConfig
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger

class EC2CreateAMI(AWSConfig):

    def __init__(self, config, logger):
        AWSConfig.__init__(self, config, logger)

        self.conn = boto.ec2.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key)

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
    check_args(2, "<output_dir> <config_path>")
    user_path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    EC2CreateAMI(cfg, logger)


