# FROM adoptopenjdk/openjdk11:jdk11u-ubuntu-nightly
FROM gcr.io/buildpacks/builder:v1
USER root
RUN apt-get update && apt-get install -y --no-install-recommends \
  ffmpeg
USER cnb