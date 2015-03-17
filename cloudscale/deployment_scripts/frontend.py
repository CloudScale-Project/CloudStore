import os
import time
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_keypair
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_instance
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_loadbalancer
from cloudscale.deployment_scripts.scripts.software import deploy_showcase
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_ami
from cloudscale.deployment_scripts.scripts.infrastructure.aws import aws_create_autoscalability
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_showcase_instances
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_balancer_instance



class Frontend:

    def __init__(self, config, logger):
        self.config = config
        self.logger=logger

    def setup_aws_frontend(self):
        self.logger=self.logger
        self.cfg = self.config.cfg
        self.config = self.config
        self.file_path = "/".join(os.path.abspath(__file__).split('/')[:-1])
        self.showcase_location = self.cfg.get('MYSQL', 'showcase_war_url')
        self.config.save('infrastructure', 'remote_user', self.cfg.get('EC2', 'remote_user'))
        self.remote_user = self.cfg.get('infrastructure', 'remote_user')
        self.remote_deploy_path = self.cfg.get('software', 'remote_deploy_path')
        self.db_num_instances = int(self.cfg.get('RDS', 'num_replicas')) + 1
        self.database_name = self.cfg.get('RDS', 'database_name')
        self.database_user = self.cfg.get('RDS', 'database_user')
        self.database_pass = self.cfg.get('RDS', 'database_pass')
        self.deploy_name = "showcase-1-a"
        self.connection_pool_size = self.cfg.get('RDS', 'connection_pool_size')

        i = aws_create_keypair.CreateKeyPair(
            cfg=self.cfg,
            user_path=self.config.user_path
        )
        i.create()

        self.config.save('EC2', 'key_pair', "%s/%s.pem" % (self.config.user_path, self.config.cfg.get('EC2', 'key_name')))

        self.key_pair = self.cfg.get('EC2', 'key_pair')
        showcase_url = None
        if self.cfg.get('EC2', 'is_autoscalable') == 'no':
            instances = []
            i = aws_create_instance.CreateEC2Instance(cfg=self.config.cfg, logger=self.logger)
            ip_addresses = []
            num_instances = int(self.cfg.get('COMMON', 'num_instances'))
            for _ in xrange(num_instances):
                instance = i.create()
                instances.append(instance)
                ip_addresses.append(instance.ip_address)

            self.config.save('infrastructure', 'ip_address', ','.join(ip_addresses))



            self.ip_addresses = self.cfg.get('infrastructure', 'ip_address').split(",")
            loadbalancer = None
            if len(instances) > 1:
                i = aws_create_loadbalancer.CreateLoadbalancer(
                    instances=instances,
                    config=self.config,
                    logger=self.logger
                )
                loadbalancer = i.create()

            deploy_showcase.DeploySoftware(self)

            showcase_url = loadbalancer.dns_name if loadbalancer else instances[0].ip_address

        elif self.cfg.get('EC2', 'is_autoscalable') == 'yes':
            i = aws_create_instance.CreateEC2Instance(cfg=self.config.cfg, logger=self.logger)
            instance = i.create()
            self.config.save('infrastructure', 'ip_address', instance.ip_address)
            self.config.save('infrastructure', 'remote_user', 'ubuntu')
            self.ip_addresses = self.cfg.get('infrastructure', 'ip_address').split(",")

            deploy_showcase.DeploySoftware(self)

            aws_create_ami.EC2CreateAMI(config=self.config, logger=self.logger)

            autoscalability = aws_create_autoscalability.Autoscalability(
                cfg=self.cfg,
                logger=self.logger
            )
            showcase_url = autoscalability.create()

        time.sleep(60)
        return showcase_url

    def setup_openstack_frontend(self):
        openstack_create_showcase_instances.CreateInstance(self.config, self.logger)
        public_ip = openstack_create_balancer_instance.CreateInstance(self.config, self.logger).get_public_ip()
        return public_ip
