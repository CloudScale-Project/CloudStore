##Introduction
Here you can find a Django app for deploying the showcase for CloudScale project on different cloud providers.

Scripts are organized in three layers according to cloud environment (infrastructure, platform, software). You can find the scripts in ```scripts/``` directory. 

##Requirements
All requirements are in **requirements.txt** file, and you can install them with **pip**:

```
$ pip install -r requirements.txt
``` 

##Usage
This application support deployment of showcase on Amazon Web Services (AWS) and OpenStack. You can deploy showcase on AWS using RDS and  with or without autoscalability enabled. On Openstack you can deploy showcase using MySQL cluster or MongoDB sharding.

To run Django app, you first need to generate the database:

```
$ python manage.py syncdb
```

Before you can use and run app, you must configure the app in ```scripts/config.ini``` file. 

Now run the Django app and access to it on http://localhost:8000/form URL.

```
$ python manage.py runserver
```

We have also provided a Fabric file for deploying showcase on production servers. Edit *fabfile.py* and edit ```env.*``` settings.

If you want to first install necessary software on your server you can do that by:

```
$ fab setup
```

and then deploy the app:

```
$ fab deploy:install=True
```

##Configuration
**Notice**: If you find `[infrastructure]`, `[software]` or `[platform]` sections in `config.ini`,
please ignore them. These sections are created internally with our scripts and all          

Before running any script you should also set the path to `mysql` and `mvn` commands in files:
configs will be overwritten.
* `platform/aws/generate.sh`
* `platform/aws/dump.sh`
* `software/deploy-amazon.sh`

Main configurations are in `config.ini` file and their meaning is described below:

### Amazon Web Services
Configuration properties for AWS:

* `aws_access_key_id` - Your AWS access key
* `aws_secret_access_key` - Your AWS secret key
* `region` - Default region where instances are created
* `availability_zones` - Comma separated availability zones for region
* `ami_id` - Base operating system AMI ID. Default value is `ami-480bea3f`, Ubuntu 12 LTS.
* `instance_type` - Default [instance type](http://aws.amazon.com/ec2/instance-types/) for your EC2 instances.
* `key_name` - The **name** of your key-pair. Usually the name without `.pem` suffix.
* `key_pair` - Full path to your key-pair file that ends with `.pem`.

Configuration properties for RDS:

* `generate_type` - If value is `dump` then the SQL dump will be imported to RDS (**Recommended**). If value is `script` then the Java generator will be used. (**Slow, not recommended**)

    If you use `script` type for generating, please also examine files in `platform/src/main/resources/database`!

* `generate_dump_path` - Full path to dump file. You can download dump file from [here](http://cloudscale.xlab.si/github/rds-tpcw-dump-latest.sql)
* `region` - Region in which RDS instances will be created.
* `instance_type` - Instance type to use for RDS instances. We recommend enough large instance type according to your testing scenario. Each instance has limited value for concurrent users to one RDS instance (max_connections MySQL config property). Please [click here](http://dba.stackexchange.com/a/41842) for more information! 
* `master_identifier` - Identifier for master instance.
* `replica_identifier` - Identifier for replicas instances.
* `database_name` - Database name.
* `database_user` - Database user.
* `database_pass` - Database pass.
* `num_replicas` - Number of replicas.
* `driver` - Driver to use with replication.

### Openstack

OpenStack configurations

* `username` - OpenStack username
* `password` - OpenStack password
* `tenant_name` - OpenStack tenant/group name
* `auth_url` - OpenStack authentication url
* `image_name` - Name of the image to use for instances
* `instance_type` - OpenStack flavor for frontend instances
* `key_name` - Name of the key pair on OpenStack
* `key_pair` - Local path to the key pair 
* `image_username` - Username of the user to ssh on instance
* `database_type` - Database type (mysql or mongo)

MySQL configurations:

* `generate_type` - Currently only possible value is *dump*
* `generate_dump_path` - Path to MySQL dump. You can get it on [cloudscale.xlab.si](cloudscale.xlab.si/github/rds-tpcw-dump-latest.sql)
* `instance_type` - OpenStack flavor for MySQL instances
* `database_name` - Database name for MySQL
* `database_user` - Database username for MySQL
* `database_pass` - Database password for MySQL
* `num_replicas` - Number of MySQL replica instances
* `driver` - MySQL driver

MongoDB configurations:

* `generate_type` - Currently only possible value is *dump*
* `generate_dump_path` - Path to MongoDB dump. You can get it on [cloudscale.xlab.si](cloudscale.xlab.si/github/mongo-dump-tpcw-latest.tar.gz)
* `instance_type` - OpenStack flavor for MongoDB instances
* `database_name` - MongoDB database name
* `database_user` - MongoDB database username
* `database_pass` - MongoDB database password
* `num_replicas` - Number of MongoDB shards
