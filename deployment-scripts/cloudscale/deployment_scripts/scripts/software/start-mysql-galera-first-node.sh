#!/bin/bash

echo "NOTE: starting mysql service"
service mysql start --wsrep-new-cluster --wsrep_cluster_address="gcomm://"

# wait for mysql to start
RETRIES=100
COUNTER=0
false
while [ $? -ne 0 ]
do
    let COUNTER=COUNTER+1
    if [ $COUNTER -gt $RETRIES ]
    then
        echo "ERROR: Could not connect to mysql server in $RETRIES tries!"
        exit 1
    fi
    sleep 3
    echo "NOTE: $COUNTER. try to connect to mysql server."
    mysqladmin ping --host=127.0.0.1 > /dev/null 2>&1
done
echo "NOTE: Connected to mysql server"


echo "NOTE: finished"
