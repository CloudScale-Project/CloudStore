import novaclient
from cloudscale.deployment_scripts.scripts import read_config, create_user_path

class AWSConfig:

    def __init__(self, config, logger):
        self.logger = logger
        self.config = config
        self.cfg = config.cfg

        self.read_config()

    def read_config(self):
        self.showcase_location      = self.cfg.get('APPLICATION', 'distribution_url')
        self.connection_pool_size   = self.cfg.get('APPLICATION', 'connection_pool_size')
        self.access_key             = self.cfg.get('AWS', 'aws_access_key_id')
        self.secret_key             = self.cfg.get('AWS', 'aws_secret_access_key')
        self.region                 = self.cfg.get('AWS', 'region')
        self.availability_zone      = self.cfg.get('AWS', 'availability_zone')
        self.instance_type          = self.cfg.get('EC2', 'instance_type')
        self.ami_id                 = self.cfg.get('EC2', 'ami_id')
        self.key_name               = self.cfg.get('EC2', 'key_name')
        self.key_pair               = self.cfg.get('EC2', 'key_pair')
        self.remote_user            = self.cfg.get('EC2', 'remote_user')
        self.instance_identifier    = self.cfg.get('EC2', 'instance_identifier')
        self.num_instances          = self.cfg.get('EC2', 'num_instances')
        self.cooldown               = int(self.cfg.get('AUTO_SCALABILITY', 'cooldown'))
        self.is_autoscalable        = self.cfg.get('AUTO_SCALABILITY', 'enabled')
        self.is_autoscalable        = self.is_autoscalable == 'yes'
        self.database_name          = self.cfg.get('DATABASE', 'name')
        self.database_user          = self.cfg.get('DATABASE', 'user')
        self.database_password      = self.cfg.get('DATABASE', 'password')
        self.database_dump_url      = self.cfg.get('DATABASE', 'dump_url')
        self.rds_instance_type      = self.cfg.get('RDS', 'instance_type')
        self.rds_num_replicas       = int(self.cfg.get('RDS', 'num_replicas'))
        self.rds_master_identifier  = self.cfg.get('RDS', 'master_identifier')
        self.rds_replica_identifier = self.cfg.get('RDS', 'replica_identifier')

class OpenstackConfig:
    def __init__(self, config, logger):
        self.logger = logger
        self.config = config
        self.cfg = config.cfg

        self.read_config()

        self.nc = novaclient.Client(self.user, self.pwd, self.tenant, auth_url=self.url)

    def read_config(self):
        self.user                       = self.cfg.get('OPENSTACK', 'username')
        self.pwd                        = self.cfg.get('OPENSTACK', 'password')
        self.url                        = self.cfg.get('OPENSTACK', 'auth_url')
        self.tenant                     = self.cfg.get('OPENSTACK', 'tenant_name')
        self.image_name                 = self.cfg.get('APPLICATION', 'image_name')
        self.remote_user                = self.cfg.get('APPLICATION', 'image_username')

        self.instance_type              = self.cfg.get('APPLICATION', 'instance_type')

        self.num_instances              = self.cfg.get('APPLICATION', 'num_instances')
        self.key_name                   = self.cfg.get('OPENSTACK', 'key_name')
        self.key_pair                   = self.cfg.get('OPENSTACK', 'key_pair')

        self.database_type              = self.cfg.get('DATABASE', 'database_type').lower()
        self.database_instance_type     = self.cfg.get('DATABASE', 'instance_type')
        self.database_num_replicas      = self.cfg.get('DATABASE', 'num_replicas')
        self.database_name              = self.cfg.get('DATABASE', 'database_name')
        self.database_user              = self.cfg.get('DATABASE', 'database_user')
        self.database_pass              = self.cfg.get('DATABASE', 'database_pass')
        self.connection_pool_size       = self.cfg.get('DATABASE', 'connection_pool_size')
        self.mongo_image_name           = self.cfg.get('MONGODB', 'image_name')
        self.mysql_setup_type           = self.cfg.get('MYSQL', 'setup_type')
        self.mysql_image_name           = self.cfg.get('MYSQL', 'image_name')
        self.showcase_url               = self.get_showcase_url()
        self.dump_url                   = self.get_dump_url()


    def get_dump_url(self):
        if self.database_type == 'mysql':
            return self.cfg.get('MYSQL', 'dump_url')
        elif self.database_type == 'mongodb' or self.database_type == 'mongo':
            return self.cfg.get('MONGODB', 'dump_url')
        else:
            raise Exception("Wrong database type!")

    def get_showcase_url(self):
        if self.database_type == 'mysql':
            return self.cfg.get('MYSQL', 'showcase_url')
        elif self.database_type == 'mongodb' or self.database_type == 'mongo':
            return self.cfg.get('MONGODB', 'showcase_url')
        else:
            raise Exception("Wrong database type!")


class Config:

    def __init__(self, infrastructure, output_directory, config_path):
        self.provider = infrastructure
        self.config_path = config_path
        self.user_path = create_user_path(output_directory)
        self.cfg = read_config(self.config_path)

    def save(self, section, variable, value):
        self.cfg.save_option(self.config_path, section, variable, str(value))

