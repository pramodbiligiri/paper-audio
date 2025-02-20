#!/bin/bash

cd /home/etl/builds/latest

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.AI

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.AR

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.DB

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.DC

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.NI

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.OS

java -cp audiogen-1.0-SNAPSHOT.jar -Dspring.profiles.active=het \
  -Dloader.main=audiogen.main.GeneratePaperAudio org.springframework.boot.loader.PropertiesLauncher \
  -n=1 -c=cs.PL

