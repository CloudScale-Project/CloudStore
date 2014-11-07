import os
import uuid
from scripts.common.Cloudscale import read_config
from webservice.settings import BASE_DIR

class Config:

    def __init__(self, data, user_path):
        self.data = data
        self.provider = data.get('provider')
        self.db_provider = data.get('db_provider')
        self.config_path = '%s/config.ini' % user_path
        self.user_path = user_path
        self.cfg = read_config(self.config_path)

        self.fr = data.get('fr')
        self.db = data.get('db')

        if self.provider == "aws":
            self.save('EC2', 'aws_access_key_id', self.fr.get('access_key'))
            self.save('EC2', 'aws_secret_access_key', self.fr.get('secret_key'))
            self.save('EC2', 'instance_type', self.fr.get('instance_type'))
            self.save('EC2', 'key_name', 'cloudscale')


            self.save('RDS', 'generate_dump_path', '%s/scripts/rds-tpcw-dump-latest.sql' % BASE_DIR)
            self.save('RDS', 'instance_type', self.db.get('instance_type'))
            self.save('RDS', 'num_replicas', str(0) if self.db.get('num_replicas') is None else str(self.db.get('num_replicas')))
            # self.save('RDS', 'master-identifier', 'cs%s' % str(uuid.uuid4()).split('-')[0])
        else:
            if self.fr.get('own_inftastructure') == 'yes':
                self.save('OPENSTACK', 'username', self.fr.get('username'))
                self.save('OPENSTACK', 'password', self.fr.get('password'))
                self.save('OPENSTACK', 'tenant_name', self.fr.get('tenant'))
                self.save('OPENSTACK', 'auth_url', self.fr.get('auth_url'))
                self.save('OPENSTACK', 'image_name', self.fr.get('image'))
                self.save('OPENSTACK', 'image_username', self.fr.get('image_username'))
            self.save('OPENSTACK', 'instance_type', self.fr.get('flavor'))
            self.save('OPENSTACK', 'database_type', self.db_provider)

            self.save('MYSQL', 'instance_type', self.db.get('flavor'))

            self.save('MYSQL', 'num_replicas', self.db.get('num_instances'))
            self.save('MYSQL', 'connection_pool_size', self.db.get('connection_pool_size'))

            self.save('MONGODB', 'instance_type', self.db.get('flavor'))
            self.save('MONGODB', 'num_replicas', self.db.get('num_shards'))
            self.save('MONGODB', 'connection_pool_size', self.db.get('connection_pool_size'))

    def save(self, section, variable, value):
        self.cfg.save_option(self.config_path, section, variable, str(value))

