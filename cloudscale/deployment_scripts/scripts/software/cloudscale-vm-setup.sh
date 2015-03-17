#!/bin/bash

sudo apt-get install -y zip tomcat7 apache2 openjdk-7-jdk
echo deb http://apt.newrelic.com/debian/ newrelic non-free >> /etc/apt/sources.list.d/newrelic.list > /tmp/newrelic.list
sudo cp /tmp/newrelic.list /etc/apt/sources.list.d/
wget -O- https://download.newrelic.com/548C16BF.gpg
sudo apt-key add 548C16BF.gpg
sudo apt-get update
sudo apt-get install newrelic-sysmond
nrsysmond-config --set license_key=b71feb62bff6540111597c79259d1f28051af3be
sudo /etc/init.d/newrelic-sysmond start
sudo a2enmod proxy
sudo cp cloudscale.conf /etc/apache2/sites-available/
sudo a2ensite cloudscale
sudo /etc/init.d/apache2 restart
sudo /etc/init.d/tomcat7 restart
