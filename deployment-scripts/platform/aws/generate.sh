#!/bin/bash
PATH_TO_MVN_COMMAND=/usr/local/bin

if [ $# -lt 1 ]
then
    echo -e "Usage:\n$ generate.sh <path_to_config_file>"
    exit
fi

if [ $2 == "dump" ]
then
    mysql -h
cd ../generator/
${PATH_TO_MVN_COMMAND}/mvn -Pamazon-hibernate -Deu.cloudscale.showcase.generate.properties=$1 install
java -cp target/dependency/*:target/showcase-1.0.0-BUILD-SNAPSHOT.jar eu.cloudscale.showcase.generate.Generate mysql