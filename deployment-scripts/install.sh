#!/bin/bash

usage ()
{
    echo "Usage:"
    echo "$ ./install.sh <aws|openstack> <config>"
}

if [ $# -lt 2 ]
then
    usage
    exit
fi

if [ $1 == 'aws' ]
then
    export PYTHONPATH=$(pwd)/common:$(pwd):$PYTHONPATH
    python infrastructure/aws/aws-remove-all.py $2
    python platform/aws/configure-rds.py $2
    python infrastructure/aws/aws-create-instance.py $2
    python infrastructure/aws/aws-create-loadbalancer.py $2
    python software/install-tomcat-apache.py $2
    python software/deploy-showcase.py $2
    python infrastructure/aws/aws-create-ami.py $2
    python infrastructure/aws/aws-create-autoscalability.py $2
elif [ $1 == 'openstack' ]
then
    echo "Not implemented yet"
else
    usage
fi
