#!/bin/bash
sudo service mysql restart
mysql -u root -ppassword -e "GRANT REPLICATION SLAVE ON *.* TO 'slave_user'@'%' IDENTIFIED BY 'password';"
mysql -u root -ppassword -e "FLUSH PRIVILEGES;"
mysql -u root -ppassword -e "CREATE DATABASE IF NOT EXISTS tpcw; USE tpcw;"
master_file=$(mysql -u root -ppassword -s -N -e 'SHOW MASTER STATUS;' | awk '{ print $1 }')
master_position=$(mysql -u root -ppassword -s -N -e 'SHOW MASTER STATUS;' | awk '{ print $2 }')
echo "$master_file|$master_position"