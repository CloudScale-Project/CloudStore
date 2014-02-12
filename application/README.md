Showcase
========
The software in this folder is a showcase for validation of tools developed during CloudScale project.

Usage
=====

Building
--------
Before building a `war` package choose which database you want to use. Showcase support two databases, MySQL and MongoDB. Configuration files for each one are in `src/main/resources/database/`.

To build showcase for MySQL or MongoDB use Maven profiles:

```bash 
$ mvn -Pamazon-hibernate install

or

```bash
$ mvn -Pamazon-mongodb install
