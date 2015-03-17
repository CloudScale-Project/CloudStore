
# TODO: remove this
echo "root:root"|chpasswd


echo "NOTE: installing mongodb"
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | tee /etc/apt/sources.list.d/mongodb.list
apt-get update

apt-get install -y mongodb-org

service mongod stop



sed -i 's/bind_ip = 127.0.0.1/bind_ip = 0.0.0.0/g' /etc/mongod.conf
# alternative port!
sed -i 's/#port = 27017/port = 27037/g' /etc/mongod.conf



echo "NOTE: adding passwordless sudo for user $USERNAME"
echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/we-need-passwordless-sudo
chmod 0440 /etc/sudoers.d/we-need-passwordless-sudo



echo "NOTE: finished... will poweroff automatically"
# deploy script has to detect when installation is finished
sleep 5
poweroff
