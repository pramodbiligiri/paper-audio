#!/bin/bash

cd /home/etl/builds/latest

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.FetchArxivData org.springframework.boot.loader.PropertiesLauncher