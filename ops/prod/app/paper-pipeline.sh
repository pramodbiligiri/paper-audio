#!/bin/bash

cd /home/etl/builds/latest;

logfile=`date +"%F-%H-%M"-ppl.log`;

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.PaperPipeline org.springframework.boot.loader.PropertiesLauncher >> /home/etl/logs/$logfile 2>&1;
