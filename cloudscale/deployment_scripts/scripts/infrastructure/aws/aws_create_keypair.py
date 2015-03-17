from boto import ec2
import os
from boto.exception import EC2ResponseError
import sys
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class CreateKeyPair:

    def __init__(self, user_path, cfg):
        self.cfg = cfg
        self.user_path = user_path

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

        keypair.save(self.user_path)

if __name__ == '__main__':
    check_args(2, "<config_path>")
    user_path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    CreateKeyPair(user_path, cfg)