### Introduction

In this directory you will find the source code of the CloudStore, a showcase for CloudScale project.
The showcase is a web application implementing an on-line book shop following the TPC-W specifications, and it's written using the Spring framework.

Being an Eclipse project, we recommend to import this project into Eclipse and continue from there.

### Running from Eclipse

Import project into Eclipse. Before running showcase, you will need to setup Apache Tomcat server in Eclipse. Don't forget to add Maven Dependencies to the Deployment Assembly setting for this project.

To run showcase on Tomcat server, right click on project and choose Run As -> Run on server. 

### Building

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

### Generating images

You can generate images by configuring and executing the file `generate_images.sh` with Perl:

```bash
$ perl generate_images.sh
```

**NOTICE** You need to add the execute permission to `ImgGen/ImgFiles/tpcwIMG`!

This script will generate images in `main/webapp/resources/img/` directory. 

Important configuration properties in `generate_images.sh` are:

 1. `$NUM_ITEMS` - refers to the number of rows in `items` database table
 2. `$DEST_DIR` - directory where to save images
 3. `$GEN_DIR` - the location of generator script 
 
### Generating database

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
