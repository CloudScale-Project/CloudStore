#!/bin/bash

sudo apt-get install -y zip tomcat7 apache2 openjdk-7-jdk

sudo a2enmod proxy proxy_http
sudo cp cloudscale.conf /etc/apache2/sites-available/
sudo a2ensite cloudscale
sudo /etc/init.d/apache2 restart

sudo ln -s /var/lib/tomcat7/shared /usr/share/tomcat7/shared
sudo ln -s /var/lib/tomcat7/server /usr/share/tomcat7/server
sudo ln -s /var/lib/tomcat7/common /usr/share/tomcat7/common

sudo /etc/init.d/tomcat7 restart