
echo "NOTE: installing apache tomcat"
echo "nameserver 8.8.8.8" | sudo tee --append /etc/resolv.conf
sudo apt-get update
sudo apt-get install -y zip tomcat7 apache2 openjdk-7-jdk

sudo sed -i 's/^securerandom.source=file:\/dev\/urandom$/securerandom.source=file\:\/dev\/.\/urandom/' /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/java.security
sudo sed -i 's/^JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"$/JAVA_OPTS="-Djava.awt.headless=true -Xmx2024m -XX:+UseConcMarkSweepGC"/' /etc/default/tomcat7
echo "NOTE: stopping apache and tomcat"
sudo /etc/init.d/apache2 stop
sudo /etc/init.d/tomcat7 stop

echo '<VirtualHost *:80>
    ProxyPreserveHost On
    # showcase-1-a (MySQL)
    ProxyPass /showcase-1-a http://localhost:8080/showcase-1-a
    ProxyPassReverse /showcase-1-a http://localhost:8080/showcase-1-a

    # showcase-1-b (noSQL)
    ProxyPass /showcase-1-b http://localhost:8080/showcase-1-b
    ProxyPassReverse /showcase-1-b http://localhost:8080/showcase-1-b

    # showcase-0
    ProxyPass /showcase-0 http://localhost:8080/showcase-0
    ProxyPassReverse /showcase-0 http://localhost:8080/showcase-0
</VirtualHost>' | sudo tee /etc/apache2/sites-available/cloudscale.conf

echo "NOTE: enabling proxy and apache cloudscale configuration"
sudo a2enmod proxy_http
sudo a2dismod mpm_event
sudo a2enmod mpm_worker
sudo a2dissite 000-default
sudo a2ensite cloudscale

echo "" | sudo tee --append /etc/hosts
echo "127.0.0.1 cloudscale-sc" | sudo tee --append /etc/hosts
