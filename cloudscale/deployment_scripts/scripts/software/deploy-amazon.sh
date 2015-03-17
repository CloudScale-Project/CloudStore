#!/bin/bash
# Author: Simon Ivansek @ Xlab
# Email: simon.ivansek@xlab.si

if [ #$ -lt 2 ]
then
    echo -e "Usage:\n $ deploy-amazon.sh <ip_address> <key_pair_path> "
    exit(0)
fi

SERVER_HOST=${1}
REMOTE_SSH_PORT=22
REMOTE_WEBAPPS_DIR=/var/lib/tomcat7/webapps
LOCAL_WAR_PATH=showcase/target/showcase-1.0.0-BUILD-SNAPSHOT.war
KEYPAIR_PATH=${2}

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
cd showcase
mvn -P amazon-hibernate clean install
echo "###########################"
echo "# DEPLOYING MYSQL VERSION #"
echo "###########################"
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 stop"
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo rm -rf ${REMOTE_WEBAPPS_DIR}/showcase-1*"
scp -i ${KEYPAIR_PATH} ${LOCAL_WAR_PATH} ubuntu@${SERVER_HOST}:${REMOTE_WEBAPPS_DIR}/showcase-1-a.war
#ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 start"

#echo "##########################"
#echo "# BUILDING noSQL VERSION #"
#echo "##########################"
#mvn -P amazon-mongodb clean install
#echo "###########################"
#echo "# DEPLOYING noSQL  VERSION #"
#echo "###########################"
#ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 stop"
#scp -i ${KEYPAIR_PATH} ${LOCAL_WAR_PATH} ubuntu@${SERVER_HOST}:${REMOTE_WEBAPPS_DIR}/showcase-1-b.war
ssh -i ${KEYPAIR_PATH} ubuntu@${SERVER_HOST} "sudo /etc/init.d/tomcat7 start"
echo ""
echo "Finished!"
done
