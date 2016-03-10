#!/bin/bash

if [ "$#" -ne 1 ]
then
	echo "Illegal number of parameters"
	echo "Usage: generate.sh <sql|mongodb>"
	exit 1
fi

DB_TYPE=$1

echo "########################"
echo "# Compiling CloudStore #"
echo "########################"

mvn install

echo "#######################"
echo "# Generating database #"
echo "#######################"

cd src/main/java

if [ $DB_TYPE == "sql" ]
then
	java -cp ../../../target/dependency/*:../../../target/classes/ -Deu.cloudscale.datasource=hibernate eu/cloudscale/showcase/generate/Generate sql
fi

if [ $DB_TYPE == "mongodb" ]
then
	java -cp ../../../target/dependency/*:../../../target/classes/ -Deu.cloudscale.datasource=mongodb eu/cloudscale/showcase/generate/Generate mongodb
fi
