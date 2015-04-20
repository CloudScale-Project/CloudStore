## Introduction

Here is the source code of CloudStore the showcase for CloudScale Project. It is web application written in Java SpringFramework according to TPC-W specification.

You can read more about CloudScale project on: http://www.cloudscale-project.eu

## Running from Eclipse

Import project into Eclipse. Before running showcase, you will need to setup Apache Tomcat server in Eclipse. Don't forget to add Maven Dependencies to the Deployment Assembly setting for this project.

To run showcase on Tomcat server, right click on project and choose Run As -> Run on server. 

## Building

Before building a war package choose which database you want to use. Showcase support two databases, MySQL and MongoDB. Configuration files for each one of them are in `src/main/resources/database/`.

To build showcase for MySQL or MongoDB use Maven profiles:

```bash
$ mvn -Phibernate -Dconnection_pool_size=<number> install 
```

or

```bash
$ mvn -Pmongodb -Dconnection_pool_size=<number> install
```

Notice the `-Dconnection_pool_size=<number>` compile parameter. Here you set the maximum size of connection pool for database.

After building you get `.war` file in `target/` directory. You can upload this file on Tomcat or any other Java web container that is able to run `.war` files.
 
### Database

#### Dump

The dump for MySQL database is available on the following url:

http://cloudscale.xlab.si/github/rds-tpcw-dump-latest.sql

#### Generate

To generate database you need to edit `src/main/resources/app-context.xml` file and replace row:

```
<import resource="${eu.cloudscale.environment}" />
```

with

```
<import resource="hibernate.xml" /> or <import resource="mongodb.xml" />
```

You'll also need to edit a configuration for choosen database `hibernate.xml` or `mongodb.xml` and set the actual number instead of variable placeholder `${connection_pool_size}`.

Then you can run `src/main/java/eu/cloudscale/showcase/generate/Generate.java` from Eclipse
