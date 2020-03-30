FROM maven:3.6-jdk-11 AS build

ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2 -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -Djava.awt.headless=true"

WORKDIR /helidon
ADD pom.xml .

ADD src src
RUN mvn -B package

RUN echo "Test Coverage Total: $(cat /usr/src/app/target/site/jacoco/index.html | grep -oP 'Total.*?\K([0-9]{1,3})%')"

FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /usr/app/
COPY --from=build /helidon/target/websocket-speedtest-*.jar ./speedtest.jar
COPY --from=build /helidon/target/libs ./libs

RUN adduser -D helidon
USER helidon

EXPOSE 7001
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/usr/app/speedtest.jar"]
