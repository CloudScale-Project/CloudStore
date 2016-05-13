## Introduction

CloudStore is an open-source sample e-commerce web application developed following the functional requirements defined by the [TPC-W](http://www.tpc.org/tpcw/) standard, and it's goal is to be used for the analysis of cloud characteristics of systems, such as capacity, scalability, elasticity and efficiency. It was developed as a [Showcase application](http://www.cloudscale-project.eu/about/showcase/) to validate the [CloudScale](http://www.cloudscale-project.eu/) tools that were developed during that EU funded project.

The application was developed in Java using the [Spring framework](https://spring.io/) and running on a [Tomcat web application server](https://tomcat.apache.org/), while using a [MySQL database](https://www.mysql.com/). Make sure you have installed all three before attempting to run CloudStore. For compiling it you will also need [Maven](https://maven.apache.org/).

It is necessary to populate the online shop with entries. The needed static files and images can be generated with the ImgGen tool or by other means, and the database can be automatically populated with a provided Java class. For convenience, a default database dump, static images and load generation scripts are available already prepared, in order to allow you to start testing a deployment in a very short time.

Different IaaS, PaaS, Storages and Architectures can be tested with little one little changes to the code or configuration, but such test are not described in this document.

## Prerequisites
Note that CloudStore requires the following software installed system where it will be running:
* Java JDK 6+
* Maven 2
* Tomcat 7
* MySQL 5.5

## Installing

**NOTICE**: The following installation instructions were tested on Ubuntu 14.04 Linux. For other platforms you may use different commands.

1. Download source code from GitHub:

   ```bash
   $ wget https://github.com/CloudScale-Project/CloudStore/archive/v1.zip -O cloudstore.zip
   ```

2. Unpack ```cloudstore.zip```:

   ```bash
   $ unzip cloudstore.zip
   ```

3. Change directory to ```CloudStore-1```:

  ```bash
  $ cd CloudStore-1 
  ```

4. Configure the CloudStore application. See **Configuration** section of this page.

4. Compile source code with Maven from directory where ```pom.xml``` file is located:

   **NOTE**: You must choose for which database provider you want to compile CloudStore. There are two maven profiles ```hibernate``` and ```mongodb```.
   
   Example for MySQL:
   
   ```bash
   $ mvn clean install -Phibernate
   ```
   
   Example for MongoDB:
   
   ```bash
   $ mvn clean install -Pmongodb
   ```

5. Copy ```target/showcase-1.0.0-BUILD-SNAPSHOT.war``` to Tomcat:
  
   ```bash
   $ cp target/showcase-1.0.0-BUILD-SNAPSHOT.war /var/lib/tomcat7/webapps
   ```
  
6. Restart Tomcat

   ```bash
   $ sudo service tomcat7 restart
   ```	

## Database

Database can be generated for both SQL and MongoDB databases or you can use existing dumps that you import into choosen database.

#### **OPTION 1**: Generate database for SQL databases

1. In ```src/main/resources/hibernate.xml``` file make sure the following line is uncommented:
   
   ```
   <prop key="hibernate.hbm2ddl.auto">update</prop>
   ```
   
2. Make sure you have edited the configuration file for database ```src/main/resources/database/database.aws.hibernate.properties```. For more information see **Configuration** section.

3. Then run ```generate.sh``` which takes two parameters: ```<sql|mongo>``` and ```<number of items>```.

   **For example:** if you want to generate MySQL database with 1000 items, execute command:

	```
	$ generate.sh sql 1000
	```

#### **OPTION 2**: Import database for SQL databases

Because generating database from scratch is very slow we also provide a dump you can import into database. The dump is generated for 10000 books and it's available for [download](http://cloudscale.xlab.si/showcase/dumps/rds-tpcw-dump-latest.sql)

## Configuration

Before you can use and deploy CloudStore you need to tell CloudStore where the database is and how to connect to it. Since we are using Hibernate ORM to interact with database, CloudStore support multiple SQL databases (tested only with MySQL). We have also implemented support for MongoDB.

#### MySQL configuration

Edit file ```src/main/resources/database/database.hibernate.properties``` and set:

1. Configure JDBC driver
  
  **Option 1**: If you are **not** using replication:

  ```
  jdbc.driverClassName=com.mysql.jdbc.Driver
  ```

  **Option 2**: If you are using replication:
  
  ```
  jdbc.driverClassName=com.mysql.jdbc.ReplicationDriver
  ```

2. Configure JDBC url

  **Option 1**: If you are **not** using replication:
  
  ```
  jdbc.url=jdbc:mysql://<host>/<database name>?autoReconnect=true
  ```
  
  **Option 2**: If you are using replication:
  
  ```
  jdbc.url=jdbc:mysql:replication://<master hostname>,<replica1 hostname>,<replica2 hostname>/<database name>?autoReconnect=true
  ```
  
  **Example:**
  
  > ```
  > jdbc.url=jdbc:mysql:replication://master.example.com,replica1.example.com,replica2.example.com/tpcw?autoReconnect=true
  > ```
  
3. Configure connection pool size

   ```
   jdbc.pool_size=150
   ```  
  
3. Configure JDBC credentials

   ```
   jdbc.username=<username>
   jdbc.password=<password>
   ```
   
   Replace ```<username>``` and ```<password>``` placeholders with username and password you are using to connect to database.

## Running

To deploy CloudStore on public or private cloud you can use our [deployment scripts](https://github.com/CloudScale-Project/Deployment-Scripts). We have also developed the [distributed JMeter](https://github.com/CloudScale-Project/Distributed-Jmeter) scripts in order to load test the CloudStore.

Otherwise, you can hand-install CloudStore in your computer or virtual machines, and generate load with Gatling, or manually by connecting your browser to the deployed site.

For running CloudStore on your computer you will need Tomcat, Spring and MySQL database installed. 
You can also import and run CloudStore from the Eclipse IDE.
Before you can run CloudStore on Tomcat you will need to compile it into a ```.war``` archive. But before compiling the CloudStore you need to edit some configuration files.

### Configs
With the configuration files you can tell CloudStore where it can find the images, and how to connect to database.

##### src/main/resources/app.properties
In this file set the url for CSS, JavaScript and image files:

```eu.cloudscale.files.url.css``` - URL or path to folder with CSS files

```eu.cloudscale.files.url.img``` - URL or path to folder with image files

```eu.cloudscale.files.url.js``` - URL or path to folder with JavaScript files

##### src/main/resources/database/database.hibernate.properties
In this file configure the MySQL database:

```jdbc.driverClassName``` - Set to ```com.mysql.jdbc.ReplicationDriver``` if you want to use master-slave MySQL setup. Otherwise set it to ```com.mysql.jdbc.Driver``` value.

```jdbc.url``` - Connection URL to the host and database. Format is ```jdbc:mysql://<host>/<database_name>```.

**Note:**
If you want to use a master-slave MySQL setup, use ```jdbc:mysql:replication://``` format, otherwise ```jdbc:mysql://```.

```jdbc.username``` - Username to connect to database

```jdbc.password``` - Password for the user to connect to database

```jdbc.hibernate.dialect``` - Hibernate dialect

### Installing
Copy compiled WAR ```target/showcase-1.0.0-BUILD-SNAPSHOT.war``` to Tomcat ```webapps/``` directory and restart Tomcat.

## Development

1. Download ZIP archive from GitHub
2. Extract ZIP archive somewhere on your filesystem
2. Open Eclipse
3. Choose File -> Import -> General -> Existing Projects into Workspace
4. Click Browser near ```Select root directory```
5. Click Finish



