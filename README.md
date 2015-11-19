## Introduction


CloudStore is an open-source e-commerce web application developed following the functional requirements defined by the [TPC-W](http://www.tpc.org/tpcw/) standard, and it's goal is to be used for the analysis of cloud characteristics of systems, such as capacity, scalability, elasticity and efficiency. It was developed as a Showcase application to validate the CloudScale tools that were developed during that project.

The application was developed in Java using the Spring framework and running on a Tomcat web application server, while using a MsSQL database.
Static files and images needs to be generated with ImgGen tool, but a default database dump and load generation scripts are available in order to start testing a deployment in a very short time.

Different IaaS, PaaS, Storages and Architectures can be tested with little one little changes to the code.

## Running

For running CloudStore on your computer you will need Tomcat and MySQL database installed. 
You can also import and run CloudStore from Eclipse IDE.
Before you can run CloudStore on Tomcat you will need to compile it into ```.war``` archive. Before compiling the CloudStore edit some config files.

### Configs
With config files you tell CloudStore where it can find the images and how to connect to database.

##### src/main/resources/app.properties
In this file set the url for CSS, JavaScript and image files:

```eu.cloudscale.files.url.css``` - URL to the folder with CSS files

```eu.cloudscale.files.url.img``` - URL to the folder with image files

```eu.cloudscale.files.url.js``` - URL to the folder with JavaScript files

#### src/main/resources/database/database.aws.hibernate.properties
In this file configure the MySQL database:

```jdbc.driverClassName``` - Set to ```com.mysql.jdbc.ReplicationDriver``` if you want to use master-slave MySQL setup. Otherwise set it to ```com.mysql.jdbc.Driver``` value.

```jdbc.url``` - Connection URL to the host and database. Format is ```jdbc:mysql://<host>/<database_name>```.

**Note:**
If you want to use master-slave MySQL setup use ```jdbc:mysql:replication://``` format, otherwise ```jdbc:mysql://```.

```jdbc.username``` - Username to connect to database

```jdbc.password``` - Password for the user to connect to database

```jdbc.hibernate.dialect``` - Hibernate dialect

### Compiling
CloudStore is a Maven project so you will have to install the Maven tool.

When you installed Maven you can compile CloudStore into ```war``` archive with executing the following command:

```bash
$ mvn clean install
```

from directory where the ```pom.xml``` file is located.

### Installing
Copy ```target/showcase-1.0.0-BUILD-SNAPSHOT.war``` to Tomcat.

