
# TODO: remove this
echo "root:root"|chpasswd


echo "NOTE: installing mysql wsrep galera"
apt-get update
wget https://launchpad.net/codership-mysql/5.6/5.6.16-25.5/+download/mysql-server-wsrep-5.6.16-25.5-amd64.deb
wget https://launchpad.net/galera/3.x/25.3.5/+download/galera-25.3.5-amd64.deb
dpkg -i mysql-server-wsrep-5.6.16-25.5-amd64.deb galera-25.3.5-amd64.deb
apt-get -f install -y


echo "NOTE: creating directory for mysql logs"
mkdir -p /var/log/mysql/


