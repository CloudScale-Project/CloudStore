import os
import uuid
from scripts.infrastructure.openstack import openstack_create_showcase_instances, openstack_create_balancer_instance
from scripts.infrastructure.aws import aws_create_keypair, aws_create_loadbalancer, aws_create_instance, aws_create_autoscalability, aws_create_ami
from scripts.software import deploy_showcase
from webservice.settings import BASE_DIR


class Frontend:

    def __init__(self, config, logger):
        self.config = config
        self.logger=logger

    def setup_aws_frontend(self):
        i = aws_create_keypair.CreateKeyPair(
            config=self.config,
        )
        i.create()

        self.config.save('EC2', 'key_pair', "%s/%s.pem" % (self.config.user_path, self.config.cfg.get('EC2', 'key_name')))

        showcase_url = None
        if self.config.fr.get('autoscaling') == 'no':
            instances = []
            i = aws_create_instance.CreateEC2Instance(cfg=self.config.cfg, logger=self.logger)
            ip_addresses = []
            for _ in xrange(self.config.fr.get('num_instances')):
                instance = i.create()
                instances.append(instance)
                ip_addresses.append(instance.ip_address)

            self.config.save('infrastructure', 'ip_address', ','.join(ip_addresses))
            self.config.save('infrastructure', 'remote_user', 'ubuntu')

            loadbalancer = None
            if len(instances) > 1:
                i = aws_create_loadbalancer.CreateLoadbalancer(
                    instances=instances,
                    config=self.config,
                    logger=self.logger
                )
                loadbalancer = i.create()

            deploy_showcase.DeploySoftware(config=self.config, logger=self.logger)

            showcase_url = loadbalancer.dns_name if loadbalancer else instances[0].ip_address

        elif self.config.fr.get('autoscaling') == 'yes':
            i = aws_create_instance.CreateEC2Instance(cfg=self.config.cfg, logger=self.logger)
            instance = i.create()
            self.config.save('infrastructure', 'ip_address', instance.ip_address)
            self.config.save('infrastructure', 'remote_user', 'ubuntu')

            deploy_showcase.DeploySoftware(config=self.config, logger=self.logger)

            aws_create_ami.EC2CreateAMI(config=self.config, logger=self.logger)

            autoscalability = aws_create_autoscalability.Autoscalability(
                config=self.config,
                key_pair=self.config.cfg.get('EC2', 'key_pair'),
                key_name=self.config.cfg.get('EC2', 'key_name'),
                logger=self.logger
            )
            showcase_url = autoscalability.create()

        return showcase_url

    def setup_openstack_frontend(self):
        openstack_create_showcase_instances.CreateInstance(self.config, self.logger)
        public_ip = openstack_create_balancer_instance.CreateInstance(self.config, self.logger).get_public_ip()
        return public_ip
