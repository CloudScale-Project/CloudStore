#!/bin/bash

# TODO: remove this
echo "root:root"|chpasswd


echo "NOTE: installing load balancer and pip"
apt-get update
apt-get install -y haproxy python-pip

pip install python-novaclient
pip install boto

echo "NOTE: enabling load balancer"
sed -i s/'ENABLED=0'/'ENABLED=1'/ /etc/default/haproxy

echo "NOTE: creating HAProxy configuration"
cat << INCLUDE_CONFIG_FILE > /etc/haproxy/haproxy.cfg_NO_SERVERS
#####REPLACE_ME_WITH_CONFIG#####
INCLUDE_CONFIG_FILE

cp /etc/haproxy/haproxy.cfg_NO_SERVERS /etc/haproxy/haproxy.cfg

echo "NOTE: starting load balancer"
service haproxy start



base64 --decode << DECODE_ME > checker.py
###PLACEHOLDER_FOR_checker.py###
DECODE_ME

base64 --decode << DECODE_ME > config.ini
###PLACEHOLDER_FOR_config.ini###
DECODE_ME


echo "NOTE: starting script that detects showcase instances and adds them to config and reloads haproxy"
python checker.py &


echo "NOTE: finished"
