import boto, boto.exception
import boto, boto.ec2
import boto.manage.cmdshell
import time
import paramiko

import sys, os
from common.Cloudscale import *

class InstallEC2Instance:

    def __init__(self, config_path, cfg, key_pair, key_name):
        self.key_pair = key_pair
        self.key_name = key_name
        self.cfg = cfg
        self.conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        self.create_security_groups()
        instance = self.create_instance()
        self.write_config(config_path, instance)


    def create_security_groups(self):
        print "Creating security groups http and ssh ..."
        self.create_security_group('http', 'Security group for HTTP protocol', '80', '0.0.0.0/0')
        self.create_security_group('ssh', 'Security group for HTTP protocol', '22', '0.0.0.0/0')

    def create_security_group(self, name, description, port, cidr):
        try:
            self.conn.create_security_group(name, description)
            self.conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise

    def create_instance(self):
        print "Creating EC2 instance ..."
        res = self.conn.run_instances(self.cfg.get('EC2', 'ami_id'), key_name=self.key_name, instance_type=self.cfg.get('EC2','instance_type'),security_groups=['http', 'ssh'])
        self.wait_available(res.instances[0])
        instance = self.conn.get_all_instances([res.instances[0].id])[0].instances[0]

        return instance

    def wait_available(self, instance):
        print "Waiting for instance to become available\nPlease wait . .",
        status = self.conn.get_all_instances([instance.id])[0].instances[0].state
        i=1
        while status != 'running':
            if i%10 == 0:
                print "\nPlease wait .",
            print ".",
            status = self.conn.get_all_instances([instance.id])[0].instances[0].state
            time.sleep(3)
            i=i+1

        print "Instance is running!"


    def write_config(self, config_path, instance):
        self.cfg.save_option(config_path, 'infrastructure', 'remote_user', 'ubuntu')
        self.cfg.save_option(config_path, 'infrastructure', 'ip_address', instance.ip_address)
        # f = open(os.path.abspath('../infrastructure.ini'), 'w')
        # f.write('[EC2]\n')
        # f.write('remote_user=ubuntu\n')
        # f.write('ip_address=' + instance.ip_address + '\n')
        # f.close()

if __name__ == '__main__':
    check_args(1, "<config_path>")
    config_path, cfg, key_name, key_pair = parse_args()
    InstallEC2Instance(config_path, cfg, key_pair, key_name)
