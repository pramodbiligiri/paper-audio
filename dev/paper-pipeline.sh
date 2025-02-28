#!/bin/bash

mvn spring-boot:run -Dspring-boot.run.fork=false -Dspring-boot.run.profiles=dev,native \
  -Dstart-class=audiogen.main.PaperPipeline