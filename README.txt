==================
Distributed JMeter
==================

Distributed JMeter application is a load generator application which was developed for CloudScale project, but it can be
 used independently from CloudScale project. For generating the load it uses the opensource software Apache JMeter.
Distributed JMeter can be deployed on AWS or OpenStack. For more information how to do it, see below.

You can read more about CloudScale project on: http://www.cloudscale-project.eu

Configs
=======

Settings in config files are separated into sections for easier understanding.

Amazon Web Services
-------------------
[SHOWCASE]

```autoscalable``` - It's value ```yes``` or ```no``` tells application if showcase is deployed in autoscalable mode. This is important for getting the data from AWS.
```host``` - The host name where showcase is deployed. Showcase must be deployed on ```/showcase-1-a``` path
```frontend_instances_id``` - The name of frontend instances of showcase. It is used for getting data from showcase instances.

[SCENARIO]

```num_threads``` - The number of threads that we want to simulate. One JMeter instance can handle 2000 VU.
```ips``` - IP addresses of instances to deploy JMeter on. Leave empty to not use this setting.
```jmeter_url``` - URL to JMeter distribution. You can download JMeter and modify it, upload it somewhere and replace existing URL with yours. Otherwise leave as it is.

[AWS]

```region``` - The region name where to deploy application.
```aws_access_key_id``` - Your AWS access key.
```aws_secret_access_key``` - Your AWS secret key.
```availability_zones``` - Availability zones for region.

[EC2]

```instance_type``` - EC2 instance type for distributed JMeter
```remote_user``` - Virtual Machine user name for SSH access
```ami_id``` - Amazon Machine Image ID to provision VM from.
```key_name``` - Only the name of SSH key for connecting to VM.
```key_pair``` - Path to SSH key for connecting to VM. It is auto-generated.

[RDS]
```identifiers``` - Name of VM for RDS database.

OpenStack
---------
[SHOWCASE]

```host``` - The host name where showcase is deployed. Showcase must be deployed on ```/showcase-1-a``` path
```frontend_instances_id``` - The name of frontend instances of showcase. It is used for getting data from showcase instances.

[SCENARIO]

```num_threads``` - The number of threads that we want to simulate. One JMeter instance can handle 2000 VU.
```instance_names``` - Name of instances on OpenStack to deploy distributed JMeter on.
```jmeter_url``` - URL to JMeter distribution. You can download JMeter and modify it, upload it somewhere and replace existing URL with yours. Otherwise leave as it is.

[OPENSTACK]

```user``` - User for authentication to OpenStack.
```pwd``` - Password for user for authentication to OpenStack.
```tenant``` - Tenant name.
```url``` - URL to your OpenStack authentication.
```image``` - Image name to use for VM.
```instance_type``` - Flavor name to use with VM.
```key_name``` - The name of SSH key on OpenStack.
```key_pair_path``` - Path to SSH key.
```remote_user``` - Username to use for SSH on VM.

Usage
=========

Amazon Web Services
-------------------
To run distributed JMeter on AWS edit ```bin/config.aws.ini``` file and run:

```
$ python run.py aws config.aws.ini scenarios/cloudscale-max.jmx
```

from ```bin/``` directory.

OpenStack
---------

To run distributed JMeter on OpenStack edit ```bin/config.openstack.ini``` file and run:

```
$ python run.py openstack config.openstack.ini scenarios/cloudscale-max.jmx
```

from ```bin/``` directory.
