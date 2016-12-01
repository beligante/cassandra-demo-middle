#!/bin/bash

#docker run --link admiring_noether:cassandra -p 8080:8080 cassandrademo $(docker inspect --format='{{ .NetworkSettings.IPAddress }}' admiring_noether)

java -jar app.jar "$@"
