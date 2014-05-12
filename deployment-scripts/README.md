##Introduction
In this folder you will find deployment scripts for showcase for Cloudscale project.

Scripts are organized in three layers according to cloud environment (infrastructure, platform, software). The ```install.sh``` script encapsulate everything together, but you can execute every script seperatelly.

##Requirements

**MySQL**

You only need `mysql` command if you use *dump* value for `generate_type` in config.ini (see below) to import data into RDS databases.

On Debian based linux distributions you can do this by:

`$ sudo apt-get install mysql-client`

Please also look in to `platform/aws/dump.sh` file and set the path to the `mysql` command.

**Maven**

Showcases are built with Maven, so you need `mvn` tool to build showcases.

On Debian based linux distributions you can install it by:

`$ sudo apt-get install maven`

**Other**

You can install these requirements with `pip` or `easy_install`:

* paramiko
* boto

##Usage
We provided scripts for deploying showcase on Amazon Web Services (AWS) and OpenStack. On AWS the showcase is deployed into autiscalability group. On OpenStack is deployed in cluster of 4 machines. 

**Warning**: At the moment we may not provide scripts for OpenStack. 

Every script takes at least one argument, that is a path to `config.ini` file. If you run script without parameters the usage message for script will be displayed.

Before you run scripts please examine the `config.ini` file (see Configuration section). For AWS you need to provide your [AWS credentials](http://aws.amazon.com/iam/) and [key-pair](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html) name and path. 

You can also use `install.sh` script which encapsulates running the scripts in one file. Run it with command:

```
$ install.sh aws /path/to/config.ini
```

if you want to deploy showcase on Amazon Web Services, or:

```
$ install.sh openstack
```

if you want to deploy showcase on OpenStack.

##Configuration
**Notice**: If you find `[infrastructure]`, `[software]` or `[platform]` sections in `config.ini`,
please ignore them. These sections are created internally with our scripts and all          

Before running any script you should also set the path to `mysql` and `mvn` commands in files:
configs will be overwritten.
* `platform/aws/generate.sh`
* `platform/aws/dump.sh`
* `software/deploy-amazon.sh`

Main configurations are in `config.ini` file and their meaning is described below:

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

