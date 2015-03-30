
echo "NOTE: starting mysql service"
service mysql start --wsrep-new-cluster --wsrep_cluster_address="gcomm://"

# wait for mysql to start
RETRIES=100
COUNTER=0
false
while [ $? -ne 0 ]
do
    #service mysql start
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

echo "NOTE: waiting for dump.sql to be uploaded"
while [ ! -f /home/$USERNAME/finished ]
do
  sleep 2
done

sleep 5

echo "NOTE: creating user and database"
echo "create database $DB_NAME;" | mysql --host=127.0.0.1
echo "grant all privileges on $DB_NAME.* to $DB_USERNAME@'%' identified by '$DB_PASSWORD';" | mysql --host=127.0.0.1


echo "NOTE: importing dump file"
mysql --host=127.0.0.1 $DB_NAME < /home/$USERNAME/dump.sql
if [ $? -eq 0 ]
then
    echo "NOTE: dump imported successfully"
else
    echo "ERROR: dump import failed!"
fi

rm /home/$USERNAME/dump.sql



echo "NOTE: finished... will poweroff automatically"
# deploy script has to detect when installation is finished
sleep 5
poweroff
