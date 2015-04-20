## CloudScale deployment scripts
CloudScale deployment scripts are Python scripts for deploying showcase for CloudScale project on Amazon Web Services
and Openstack. The showcase is a book store written in Java Spring Framework according to TPC-W standard. Scripts are
configurable so you ca also use them for deploying your application to Amazon Web Services or Openstack.

## About CloudScale project
The goal of CloudScale is to aid service providers in analysing, predicting and resolving scalability issues,
i.e., support scalable service engineering. The project extends existing and develops new solutions that support
the handling of scalability problems of software-based services.

You can read more about CloudScale project on: http://www.cloudscale-project.eu

## Installation
To install scripts download zip or checkout repository and then run:

```
$ python setup.py install
```

This will install CloudScale deployment scripts to your ```site-packages``` folder of your Python distribution.

If you want to install it with ```pip``` you can do this by running the following command:

```
$ pip install -e https://github.com/CloudScale-project/Showcase/deployment-scripts/zipball/deployment-scripts
```

## Usage
You can run scripts in ```cloudscale/deployment_scripts/scripts/``` as standalone or use them as part of your application. The example of using scripts as part of your
application is in ```bin/run.py``` file.

### Amazon Web Services
To deploy showcase on Amazon Web Services edit ```bin/config.aws.ini``` file and run:

```
$ python run.py aws config.aws.ini
```

### Openstack
To deploy MySQL version of showcase on OpenStack edit ```bin/config.openstack.mysql.ini``` and run:

```
$ python run.py openstack config.openstack.mysql.ini
```

To deploy MongoDB version of showcase on OpenStack edit ```bin/config.openstack.mongo.ini``` and run:

```
$ python run.py openstack config.openstack.mongo.ini
```
