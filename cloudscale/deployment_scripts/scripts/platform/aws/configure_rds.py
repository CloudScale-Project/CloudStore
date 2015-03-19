import urllib
import boto.exception
import boto, boto.ec2, boto.rds
import boto.manage.cmdshell
import time
import subprocess
import os
import sys
from cloudscale.deployment_scripts.config import Setup
from cloudscale.deployment_scripts.scripts import check_args, get_cfg_logger


class ConfigureRDS(Setup):

    def __init__(self, config, logger):
        Setup.__init__(self, config, logger)

        self.conn = boto.rds.connect_to_region(self.region,
                                               aws_access_key_id=self.access_key,
                                               aws_secret_access_key=self.secret_key
        )

        sg_id = self.create_security_group('mysql', 'Security group for MYSQL protocol', '3306', '0.0.0.0/0')
        instance = self.create_master(sg_id)
        self.import_data(instance)

        replicas_urls = []
        if self.rds_num_replicas > 0:
            replicas_urls = self.create_read_replicas()

        self.write_config(instance.endpoint[0], replicas_urls)

    def create_read_replicas(self):
        urls = []
        instance_ids = []
        for i in xrange(self.rds_num_replicas):
            self.logger.log("Creating read replica " + str(i+1))
            try:
                instance = self.conn.create_dbinstance_read_replica(
                    self.rds_replica_identifier + str(i+1),
                    self.rds_master_identifier,
                    self.rds_instance_type,
                    availability_zone="eu-west-1a"
                )
            except boto.exception.BotoServerError as e:
                if not e.error_code == 'DBInstanceAlreadyExists':
                    raise
                else:
                    id = self.rds_replica_identifier + str(i+1)
                    self.logger.log("Modifying RDS %s" % id)
                    self.conn.modify_dbinstance(id=id, instance_class=self.rds_instance_type, apply_immediately=True)
                    time.sleep(60)

        for i in xrange(self.rds_num_replicas):
            instance = self.conn.get_all_dbinstances(instance_id=self.rds_replica_identifier + str(i+1))[0]
            self.wait_available(instance)
            instance = self.conn.get_all_dbinstances(instance_id=self.rds_replica_identifier + str(i+1))[0]
            urls.append(instance.endpoint[0])

        return urls


    def import_data(self, instance):
        self.logger.log("Importing data. This may take a while, please wait ...")
        generate_type = "dump"

        if generate_type == "script":
            config_path = self.write_showcase_database_config(instance)
            self.generate(config_path)
        elif generate_type == "dump":
            self.dump(instance)
        else:
            msg = "Wrong generate type for import data!"
            logger.error(msg)
            raise Exception(msg)

        self.logger.log("Successfully imported data")

    def dump(self, instance):
        dump_file = "/tmp/cloudscale-dump.sql"
        if not os.path.isfile(dump_file):
            urllib.urlretrieve(self.database_dump_url, dump_file)

        cmd = [os.path.dirname(__file__) + "/dump.sh", str(instance.endpoint[0]), self.database_user, self.database_password, self.database_name, dump_file]
        subprocess.check_output(cmd)

    def write_showcase_database_config(self, instance):
        path = os.path.abspath('../generator/src/main/resources/generate/database.properties')
        self.logger.log(path)
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
        ec2_conn = boto.ec2.connect_to_region(self.region,
                                              aws_access_key_id=self.access_key,
                                              aws_secret_access_key=self.secret_key)
        try:
            ec2_conn.create_security_group(name, description)
            ec2_conn.authorize_security_group(group_name=name, ip_protocol='tcp', from_port=port, to_port=port, cidr_ip=cidr)

            self.conn.create_dbsecurity_group(name, description)
            self.conn.authorize_dbsecurity_group(name, cidr, name)
        except boto.exception.EC2ResponseError as e:
            if str(e.error_code) != 'InvalidGroup.Duplicate':
                raise e
        finally:
            return ec2_conn.get_all_security_groups(groupnames=['mysql'])[0].id

    def create_master(self, sg_id):

        self.logger.log("Creating RDS master instance ...")

        try:
            instance = self.conn.create_dbinstance(
                self.rds_master_identifier,
                5,
                self.rds_instance_type,
                self.database_user,
                self.database_password,
                db_name=self.database_name,
                vpc_security_groups=[sg_id],
                availability_zone='eu-west-1a',
                backup_retention_period=1
            )
        except boto.exception.BotoServerError as e:
            if not e.error_code == 'DBInstanceAlreadyExists':
                raise Exception(e)
            else:
                id = self.cfg.get('RDS', 'master_identifier')
                self.logger.log("Modifying RDS %s ..." % id)
                self.conn.modify_dbinstance(id=id, instance_class=self.rds_instance_type, apply_immediately=True)
                #time.sleep(60)
        finally:
            instance = self.conn.get_all_dbinstances(instance_id=self.rds_master_identifier)[0]

        self.wait_available(instance)

        instance = self.conn.get_all_dbinstances(instance_id=self.rds_master_identifier)[0]

        return instance


    def wait_available(self, instance):
        self.logger.log("Waiting for instance to become available\nPlease wait . .")
        status = self.conn.get_all_dbinstances(instance.id)[0].status
        i=1
        while status != 'available':
            if i%10 == 0:
                self.logger.log("\nPlease wait .")
            self.logger.log('.', append_to_last=True)
            status = self.conn.get_all_dbinstances(instance.id)[0].status
            time.sleep(3)
            i=i+1

        time.sleep(5)

        self.logger.log("Instance is running!")

    def write_config(self, master_url, replica_urls=[]):
        urls = master_url
        if len(replica_urls) > 0:
            urls += "," + ",".join(replica_urls)
        self.config.save('platform', 'urls', urls)
        # f = open(os.path.abspath('../platform.ini'), 'w')
        # f.write('[Database]\n')
        # f.write('db_urls=' + master_url)
        # for url in replica_urls:
        #     f.write(',' + url)

if __name__ == "__main__":
    check_args(2, "<output_dir> <config_path>")
    path, cfg, logger = get_cfg_logger(sys.argv[1], sys.argv[2])
    ConfigureRDS(cfg, logger)