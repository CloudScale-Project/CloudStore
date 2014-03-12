<<<<<<< HEAD
##Introduction
Here you will find a modernized version of TPC-W e-commerce benchmark applicatin. Basically the application is a very simple bookshop. The modernized version is written in SpringFramework and uses Maven for building.

##Building
Before building a `war` package choose which database you want to use. Showcase support two databases, MySQL and MongoDB. Configuration files for each one are in `src/main/resources/database/`.

To build showcase for MySQL or MongoDB use Maven profiles:

```bash 
$ mvn -Pamazon-hibernate install
```

or

```bash
$ mvn -Pamazon-mongodb install
```
=======
Showcase
>>>>>>> 60bb954... Initial commit
