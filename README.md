## Introduction

CloudStore is an e-commerce web application developed by using [TPC-W](http://www.tpc.org/tpcw/) standard and it is used as a Showcase application to validate the CloudScale tools that were developed during the project. The application was developed using the Java Springframework.

CloudStore runs on Tomcat web application server and uses MySQL database. Static files and images needs to be generated with ImgGen tool.

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
Copy ```target/showcase-1.0-SNAPSHOT.war``` to Tomcat.

