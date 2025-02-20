#!/bin/bash

mvn spring-boot:run -Dspring-boot.run.fork=false -Dspring.profiles.active=dev \
  -Dstart-class=audiogen.main.PaperPipeline