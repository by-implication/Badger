#!/bin/sh
if [ -z $1 ]; then
	DATABASE="budget"
else
	DATABASE=$1
fi
echo $DATABASE
psql -Upostgres -c"drop database $DATABASE"
psql -Upostgres -c"create database $DATABASE"
