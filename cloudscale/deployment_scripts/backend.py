from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_mysql_instances
from cloudscale.deployment_scripts.scripts.platform.aws import configure_rds
from cloudscale.deployment_scripts.scripts.infrastructure.openstack import openstack_create_mongodb_instances
class Backend:

    def __init__(self, config, logger):
        self.config = config
        self.logger = logger

    def setup_rds(self):
        configure_rds.ConfigureRDS(self.config, self.logger)

    def setup_openstack_mysql(self):
        openstack_create_mysql_instances.ConfigureMySQL(self.config, self.logger)

    def setup_openstack_mongodb(self):
        openstack_create_mongodb_instances.ConfigureMongodb(self.config, self.logger)