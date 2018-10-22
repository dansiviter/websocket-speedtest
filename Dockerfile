FROM openjdk:8-jdk-alpine
MAINTAINER Dan Siviter [dansiviter@gmail.com]

ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/websocket-echo.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /opt/websocket-echo.jar