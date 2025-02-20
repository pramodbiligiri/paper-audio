FROM adoptopenjdk/openjdk11:jdk11u-ubuntu-nightly
USER root
RUN apt-get update && apt-get install -y --no-install-recommends \
  ffmpeg

ARG JAR_FILE=target/paper-audio-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-cp", "/app.jar", \
   "-Dloader.main=audiogen.main.NoOp", "org.springframework.boot.loader.PropertiesLauncher"]
