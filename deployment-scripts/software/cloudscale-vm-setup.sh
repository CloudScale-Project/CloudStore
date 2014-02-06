#!/bin/bash

sudo apt-get install -y zip tomcat7 apache2 openjdk-7-jdk

sudo a2enmod proxy
sudo cp cloudscale.conf /etc/apache2/sites-available/
sudo a2ensite cloudscale
sudo /etc/init.d/apache2 restart
sudo /etc/init.d/tomcat7 restart
