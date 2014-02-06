#!/bin/bash
# Author: Simon Ivansek @ Xlab
# Email: simon.ivansek@xlab.si

SERVERS[0]=
SERVERS[1]=
SERVERS[2]=
SERVERS[3]=
SERVERS[4]=

for ip in "${SERVERS[@]}"
do
SERVER_HOST=${ip}
REMOTE_SSH_PORT=22
REMOTE_WEBAPPS_DIR=/var/lib/tomcat7/webapps
LOCAL_WAR_PATH=target/showcase-1.0.0-BUILD-SNAPSHOT.war
KEYPAIR_PATH=/path/to/keypair.pem

echo "########## NOTICE ###########"
echo "For this deployment you need to have a ssh key generated. Your public key (.pub) needs"
echo "to be authorized on remote server. How to do this see:"
echo "http://www.dotkam.com/2009/03/10/run-commands-remotely-via-ssh-with-no-password/"
echo ""
echo "Is ${SERVER_HOST} on port ${REMOTE_SSH_PORT} available?"
if ! [ "`nc -zvv ${SERVER_HOST} ${REMOTE_SSH_PORT}`" ]; then
	echo "No. Terminating ..."
	exit;
else
	echo  "Yes. Contuining ..."
fi

echo "##########################"
echo "# BUILDING MYSQL VERSION #"
echo "##########################"
mvn -P amazon-hibernate clean install
echo "###########################"
echo "# DEPLOYING MYSQL VERSION #"
echo "###########################"
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 stop"
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo rm -rf ${REMOTE_WEBAPPS_DIR}/showcase-1*"
scp -i ${KEYPAIR_PATH} ${LOCAL_WAR_PATH} ubuntu@${SERVER_HOST}:${REMOTE_WEBAPPS_DIR}/showcase-1-a.war
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 start"

echo "##########################"
echo "# BUILDING noSQL VERSION #"
echo "##########################"
mvn -P amazon-mongodb clean install
echo "###########################"
echo "# DEPLOYING noSQL  VERSION #"
echo "###########################"
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 stop"
scp -i ${KEYPAIR_PATH} ${LOCAL_WAR_PATH} ubuntu@${SERVER_HOST}:${REMOTE_WEBAPPS_DIR}/showcase-1-b.war
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 start"
echo ""
echo "Finished!"
done
