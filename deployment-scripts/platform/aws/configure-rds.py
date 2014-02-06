import boto, boto.exception
import boto, boto.ec2, boto.rds
import boto.manage.cmdshell
import time
import re
import paramiko
import subprocess
import sys, os
from common.Cloudscale import *

class ConfigureRDS:

    def __init__(self, config_path, cfg, key_pair, key_name):
        self.config_path = config_path
        self.db_password= cfg.get('RDS', 'database_pass')
        self.key_pair = key_pair
        self.key_name = key_name
        self.cfg = cfg
        self.conn = boto.rds.connect_to_region(self.cfg.get('EC2', 'region'),
                                               aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                               aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        sg_id = self.create_security_group('mysql', 'Security group for MYSQL protocol', '3306', '0.0.0.0/0')
        instance = self.create_master(sg_id)
        self.import_data(instance)
        replicas_urls = self.create_read_replicas()
        self.write_config(instance.endpoint[0], replicas_urls)

    def create_read_replicas(self):
        num = self.cfg.get('RDS', 'num_replicas')
        urls = []
        for i in xrange(int(num)):
            print "Creating read replica " + str(i+1)
            try:
                instance = self.conn.create_dbinstance_read_replica('cloudscale-replica' + str(i+1), 'cloudscale-master', 'db.t1.micro', availability_zone="eu-west-1a")
            except boto.exception.BotoServerError as e:
                if not e.error_code == 'DBInstanceAlreadyExists':
                    raise
            finally:
                instance = self.conn.get_all_dbinstances(instance_id='cloudscale-replica' + str(i+1))[0]

            self.wait_available(instance)

            instance = self.conn.get_all_dbinstances(instance_id='cloudscale-replica' + str(i+1))[0]
            urls.append(instance.endpoint[0])

        return urls


    def import_data(self, instance):
        print "Importing data. This may take a while, please wait ..."
        generate_type = self.cfg.get('RDS', 'generate_type')

        if generate_type == "script":
            config_path = self.write_showcase_database_config(instance)
            self.generate(config_path)
        elif generate_type == "dump":
            self.dump(instance)

        print "Successfully imported data"

    def dump(self, instance):
        dump_file = os.path.abspath(self.cfg.get('RDS', 'generate_dump_path'))
        db = self.cfg.get('RDS', 'database_name')
        user = self.cfg.get('RDS', 'database_user')
        passwd = self.cfg.get('RDS', 'database_pass')
        cmd = [os.path.abspath("dump.sh"), str(instance.endpoint[0]), user, passwd, db, dump_file]
        subprocess.call(cmd)

    def write_showcase_database_config(self, instance):
        path = os.path.abspath('../generator/src/main/resources/generate/database.properties')
        print path
        f = open(path, 'w')
        f.write("jdbc.dbtype=mysql\n")
        f.write("jdbc.driverClassName=com.mysql.jdbc.Driver\n")
        f.write("jdbc.url=jdbc:mysql://" + instance.ip_address + "/tpcw\n")
        f.write("jdbc.username=root\n")
        f.write("jdbc.password=" + self.db_password + "\n")
        f.write("jdbc.hibernate.dialect=org.hibernate.dialect.MySQLDialect\n")
        f.close()
        return path

    def generate(self, config_path):
        script_path = os.path.abspath('generate.sh')
        subprocess.call([script_path, 'file:///' + config_path, self.cfg.get('RDS', 'generate_type')])


    def write_config(self, instance):
        f = open('../database.properties', 'w')
        f.write('')

    def create_security_group(self, name, description, port, cidr):
        ec2_conn = boto.ec2.connect_to_region(self.cfg.get('EC2', 'region'),
                                              aws_access_key_id=self.cfg.get('EC2', 'aws_access_key_id'),
                                              aws_secret_access_key=self.cfg.get('EC2', 'aws_secret_access_key'))
        try:
            ec2_conn.create_security_group(name, description)
            ec2_conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)

            self.conn.create_dbsecurity_group(name, description)
            self.conn.authorize_dbsecurity_group(name, cidr, name)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise
        finally:
            return ec2_conn.get_all_security_groups(groupnames=['mysql'])[0].id

    def create_master(self, sg_id):
        print "Creating RDS master instance ..."

        try:
            instance = self.conn.create_dbinstance('cloudscale-master', 5, 'db.t1.micro', 'root', self.db_password, db_name='tpcw', vpc_security_groups=[sg_id], availability_zone='eu-west-1a', backup_retention_period=0)
        except boto.exception.BotoServerError as e:
            if not e.error_code == 'DBInstanceAlreadyExists':
                raise
        finally:
            instance = self.conn.get_all_dbinstances(instance_id='cloudscale-master')[0]

        self.wait_available(instance)

        instance = self.conn.get_all_dbinstances(instance_id='cloudscale-master')[0]

        return instance

    def wait_available(self, instance):
        print "Waiting for instance to become available\nPlease wait . .",
        status = self.conn.get_all_dbinstances(instance.id)[0].status
        i=1
        while status != 'available':
            if i%10 == 0:
                print "\nPlease wait .",
            print ".",
            status = self.conn.get_all_dbinstances(instance.id)[0].status
            time.sleep(3)
            i=i+1

        time.sleep(5)

        print "Instance is running!"

    def write_config(self, master_url, replica_urls=[]):
        urls = master_url + "," + ",".join(replica_urls)
        self.cfg.save_option(self.config_path, 'platform', 'urls', urls)
        # f = open(os.path.abspath('../platform.ini'), 'w')
        # f.write('[Database]\n')
        # f.write('db_urls=' + master_url)
        # for url in replica_urls:
        #     f.write(',' + url)


if __name__ == '__main__':
    print os.path.abspath('../../common')
    sys.path.append(os.path.abspath('../../common'))

    check_args(1, "<config_path>")
    config_path, cfg, key_name, key_pair = parse_args()
    ConfigureRDS(config_path, cfg, key_pair, key_name)
