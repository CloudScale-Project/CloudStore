
echo "NOTE: wait for first node to start"
RETRIES=400
COUNTER=0
false
while [ $? -ne 0 ]
do
    let COUNTER=COUNTER+1
    if [ $COUNTER -gt $RETRIES ]
    then
        echo "ERROR: Could not connect to first node in $RETRIES tries!"
        exit 1
    fi
    sleep 3
    echo "NOTE: $COUNTER. try to connect to first node."
    mysqladmin ping --host=$FIRST_NODE_IP > /dev/null 2>&1
done
echo "NOTE: Connected to first node"



echo "NOTE: starting mysql service"
service mysql start --wsrep_cluster_address="gcomm://$FIRST_NODE_IP"

echo "NOTE: wait for mysql to start"
RETRIES=400
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
    if [ $(( $COUNTER % 40 )) -eq 0 ]
    then
        echo "NOTE: starting mysql service again"
        service mysql start --wsrep_cluster_address="gcomm://$FIRST_NODE_IP"
    fi
    echo "NOTE: $COUNTER. try to connect to mysql server."
    mysqladmin ping --host=127.0.0.1 > /dev/null 2>&1
done
echo "NOTE: Connected to mysql server"


echo "NOTE: finished"
