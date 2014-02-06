#!/bin/bash
PATH_TO_MYSQL_COMMAND=/usr/local/mysql/bin/

if [ $# -lt 5 ]
then
    echo -e "Usage:\n$ dump.sh <host> <user> <password> <database> <dump_file_path>"
    exit
fi

${PATH_TO_MYSQL_COMMAND}/mysql -h $1 -u $2 -p$3 --database=$4 < $5
