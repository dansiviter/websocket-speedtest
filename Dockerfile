FROM maven:3.6-jdk-11 AS build  
COPY src /usr/src/app/src 
COPY pom.xml /usr/src/app 
RUN mvn -B -f /usr/src/app/pom.xml package
RUN echo "Test Coverage Total: $(cat /usr/src/app/target/site/jacoco/index.html | grep -oP 'Total.*?\K([0-9]{1,3})%')"

FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build /usr/src/app/target/websocket-speedtest-*-thorntail.jar /usr/app/thorntail.jar

RUN adduser -D thorntail
USER thorntail

EXPOSE 8080  
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/usr/app/thorntail.jar"] 
