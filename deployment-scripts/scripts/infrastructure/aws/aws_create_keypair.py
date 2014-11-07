from boto import ec2
import os
from boto.exception import EC2ResponseError
from webservice.settings import BASE_DIR

class CreateKeyPair:

    def __init__(self, config):
        self.config = config
        self.cfg = config.cfg

    def create(self):
        conn = ec2.connect_to_region(
            self.cfg.get('EC2', 'region'),
            aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
            aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key')
        )

        try:
            keypair = conn.create_key_pair(self.cfg.get('EC2', 'key_name'))
        except EC2ResponseError as e:
            if e.error_code == 'InvalidKeyPair.Duplicate':
                conn.delete_key_pair(key_name=self.cfg.get('EC2', 'key_name') )
                keypair = conn.create_key_pair(self.cfg.get('EC2', 'key_name'))
            else:
                raise e

        keypair.save(self.config.user_path)
