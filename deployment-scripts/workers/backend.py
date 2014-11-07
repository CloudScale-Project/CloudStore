from scripts.infrastructure.openstack import openstack_create_mysql_instances, openstack_create_mongodb_instances
from scripts.platform.aws import configure_rds


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
