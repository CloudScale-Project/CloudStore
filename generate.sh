#!/bin/bash

if [ "$#" -ne 2 ]
then
	echo "Illegal number of parameters"
	echo "Usage: generate.sh <sql> <number of items>"
	exit 1
fi

DB_TYPE=$1
NUM_ITEMS=$2

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
	java -cp ../../../target/dependency/*:../../../target/classes/ -Deu.cloudscale.datasource=hibernate eu/cloudscale/showcase/generate/Generate sql $NUM_ITEMS
fi
