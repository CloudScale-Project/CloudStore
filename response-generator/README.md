<<<<<<< HEAD
## Distributed JMeter
Distributed JMeter application is a load generator application which was developed for CloudScale project, but it can be
 used independently from CloudScale project. For generating the load it uses the opensource software Apache JMeter.
Distributed JMeter can be deployed on AWS or OpenStack. For more information how to do it, see below.

You can read more about CloudScale project on: http://www.cloudscale-project.eu

## Configs

Settings in config files are separated into sections for easier understanding.

### Amazon Web Services

**[SHOWCASE]**

```autoscalable``` - It's value ```yes``` or ```no``` tells application if showcase is deployed in autoscalable mode. This is important for getting the data from AWS.
```host``` - The host name where showcase is deployed. Showcase must be deployed on ```/showcase-1-a``` path
```frontend_instances_id``` - The name of frontend instances of showcase. It is used for getting data from showcase instances.

**[SCENARIO]**

```num_threads``` - The number of threads that we want to simulate. One JMeter instance can handle 2000 VU.
```ips``` - IP addresses of instances to deploy JMeter on. Leave empty to not use this setting.
```jmeter_url``` - URL to JMeter distribution. You can download JMeter and modify it, upload it somewhere and replace existing URL with yours. Otherwise leave as it is.

**[AWS]**

```region``` - The region name where to deploy application.
```aws_access_key_id``` - Your AWS access key.
```aws_secret_access_key``` - Your AWS secret key.
```availability_zones``` - Availability zones for region.

**[EC2]**

```instance_type``` - EC2 instance type for distributed JMeter
```remote_user``` - Virtual Machine user name for SSH access
```ami_id``` - Amazon Machine Image ID to provision VM from.
```key_name``` - Only the name of SSH key for connecting to VM.
```key_pair``` - Path to SSH key for connecting to VM. It is auto-generated.

**[RDS]**

```identifiers``` - Name of VM for RDS database.

### OpenStack

**[SHOWCASE]**

```host``` - The host name where showcase is deployed. Showcase must be deployed on ```/showcase-1-a``` path
```frontend_instances_id``` - The name of frontend instances of showcase. It is used for getting data from showcase instances.

**[SCENARIO]**

```num_threads``` - The number of threads that we want to simulate. One JMeter instance can handle 2000 VU.
```instance_names``` - Name of instances on OpenStack to deploy distributed JMeter on.
```jmeter_url``` - URL to JMeter distribution. You can download JMeter and modify it, upload it somewhere and replace existing URL with yours. Otherwise leave as it is.

**[OPENSTACK]**

```user``` - User for authentication to OpenStack.
```pwd``` - Password for user for authentication to OpenStack.
```tenant``` - Tenant name.
```url``` - URL to your OpenStack authentication.
```image``` - Image name to use for VM.
```instance_type``` - Flavor name to use with VM.
```key_name``` - The name of SSH key on OpenStack.
```key_pair_path``` - Path to SSH key.
```remote_user``` - Username to use for SSH on VM.

## Installation

Before you can use distributed JMeter scripts you need to install them. You can do this by downloading the ZIP archive and then run:

```
$ python setup.py install 
```

You can also install the scripts using ```pip``` tool:

```
$ pip install -e https://github.com/CloudScale-project/Showcase/distributed-jmeter/zipball/distributed-jmeter
```

## Usage

### Amazon Web Services
To run distributed JMeter on AWS edit ```bin/config.aws.ini``` file and run:

```
$ python run.py aws config.aws.ini scenarios/cloudscale-max.jmx
```

from ```bin/``` directory.

### OpenStack

To run distributed JMeter on OpenStack edit ```bin/config.openstack.ini``` file and run:

```
$ python run.py openstack config.openstack.ini scenarios/cloudscale-max.jmx
```

from ```bin/``` directory.
=======
### Description

[Response generator](https://arcane-meadow-6418.herokuapp.com/) is a web application that simulates response times according to chosen distribution.

Supported distributions are:
- [uniform](http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29)
- constant
- [gauss](http://en.wikipedia.org/wiki/Normal_distribution)
- [gamma](http://en.wikipedia.org/wiki/Gamma_distribution)
- [exponentional](http://en.wikipedia.org/wiki/Exponential_distribution)
- [logarithmic](http://en.wikipedia.org/wiki/Logarithmic_distribution)
- [pareto](http://en.wikipedia.org/wiki/Pareto_distribution)
- [weibull](http://en.wikipedia.org/wiki/Weibull_distribution)

Each distribution have specific parameters by which you can manipulate the end response time in seconds.
To each distribution we added a **k** parameter which shifts the end response time for **k**.

### Usage

Response generator is designed to be used inside real application as a API call. 
Each distribution has it's own URL and accepts it's own parameters, parameter **k** and **test=[true|false]** parameter:

* ```/uniform?a=1&b=2&k=3&test=true```
* ```/constant?c=1&test=true```
* ```/expo?lambda=1&k=0&test=true```
* ```/log?mu=1&sigma=2&k=0&test=true```
* ```/gamma?alpha=1&beta=2&k=0&test=true```
* ```/gauss?mu=10&sigma=2&k=0&test=true```
* ```/log?mu=1&sigma=2&k=0&test=true```
* ```/pareto?alpha=1&k=0&test=true```
* ```/weibull?alpha=1&beta=2&k=0&test=true```

Use ```test=true``` if you want to just print the value.

Use ```test=false``` if you want to actually make a delay.

### Examples

* Gauss distribution has **mu** and **sigma** parameters and we want to just get the value:

  ```
  /gauss?mu=1&sigma=2&k=0&test=true
  ```
  
* Uniform distribution has **a** and **b** parameters and we want to make a delay:
  
  ```
  /uniform?a=1&b=2&k=0&test=false
  ```
  
* Exponentional distribution has **lambda** parameter and we want to shift the end response time for 10 units and make a delay:

  ```
  /expo?lambda=1&k=10&test=false
  ```

### Screenshots

![Screenshot](https://raw.githubusercontent.com/CloudScale-Project/Showcase/master/response-generator/static/images/screenshot.png)
>>>>>>> response-generator
