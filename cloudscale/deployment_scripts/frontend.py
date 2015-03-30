import os
import time
from cloudscale.deployment_scripts.config import AWSConfig
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_keypair
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_instance
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_loadbalancer
from cloudscale.deployment_scripts.scripts.software import deploy_showcase
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_ami
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_autoscalability
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_showcase_instances
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_balancer_instance



class Frontend(AWSConfig):

    def __init__(self, config, logger):
        AWSConfig.__init__(self, config, logger)

        self.instance_ids = []
        self.ip_addresses = []

        self.file_path = "/".join(os.path.abspath(__file__).split('/')[:-1])

        self.remote_deploy_path = self.cfg.get('software', 'remote_deploy_path')
        self.deploy_name = "showcase-1-a"

    def setup_aws_frontend(self):

        i = aws_create_keypair.CreateKeyPair(
            config=self.config,
            user_path=self.config.user_path,
            logger=self.logger
        )
        i.create()

        self.config.save('EC2', 'key_pair', "%s/%s.pem" % (self.config.user_path, self.config.cfg.get('EC2', 'key_name')))

        self.key_pair = self.cfg.get('EC2', 'key_pair')

        showcase_url = None
        if not self.is_autoscalable:
            i = aws_create_instance.CreateEC2Instance(config=self.config, logger=self.logger)

            instances = i.create_all(self.num_instances)


            for instance in instances:
                self.ip_addresses.append(instance.ip_address)


            loadbalancer = None
            if len(instances) > 1:
                i = aws_create_loadbalancer.CreateLoadbalancer(
                    config=self.config,
                    logger=self.logger
                )
                loadbalancer = i.create(instances)

            deploy_showcase.DeploySoftware(self)

            showcase_url = loadbalancer.dns_name if loadbalancer else instances[0].ip_address

        else:
            i = aws_create_instance.CreateEC2Instance(config=self.config, logger=self.logger)
            instance = i.create()
            self.config.save('infrastructure', 'ip_address', instance.ip_address)
            self.ip_addresses.append(instance.ip_address)

            deploy_showcase.DeploySoftware(self)

            aws_create_ami.EC2CreateAMI(config=self.config, logger=self.logger)

            autoscalability = aws_create_autoscalability.Autoscalability(
                config=self.config,
                logger=self.logger
            )
            showcase_url = autoscalability.create()

        time.sleep(60)
        return showcase_url

    def setup_openstack_frontend(self):
        openstack_create_showcase_instances.CreateInstance(self.config, self.logger)
        public_ip = openstack_create_balancer_instance.CreateInstance(self.config, self.logger).get_public_ip()
        return public_ip
