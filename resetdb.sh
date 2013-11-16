#!/bin/sh
if [ -z $1 ]; then
	DATABASE="budget"
else
	DATABASE=$1
fi
echo $DATABASE
/Applications/Postgres.app/Contents/MacOS/bin/psql -Ufee -c"drop database $DATABASE"
/Applications/Postgres.app/Contents/MacOS/bin/psql -Ufee -c"create database $DATABASE"
