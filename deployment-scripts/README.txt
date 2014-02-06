INTRODUCTION
------------
In this folder you will find deployment scripts for showcase for Cloudscale project.
Showcase is originally we used TPC-W showcase, rewrite it into Spring framework
For more informations see: http://www.cloudscale-project.eu.

Scripts are organized in three layers, as it is in cloud. The install.sh script
encapsulate everything together, but you can execute every script seperatelly.

REQUIREMENTS
------------
paramiko
boto

In order to use openstack you need to install these packages (use pip or easy_install):
python-novaclient

CONFIGURATION
--------------
All configurations are in config.ini file which is seperated in several sections.
To configure EC2 instances edit properties in EC2 section, same for RDS.

Note: If you find [infrastructure], [software] or [platform] sections in config.ini,
please omit them. These sections are created internally with our scripts and all
configs will be overwritten.

USAGE
-----
To
