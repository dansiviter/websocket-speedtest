FROM maven:3-jdk-8 AS build  
COPY src /usr/src/app/src 
COPY pom.xml /usr/src/app 
RUN mvn -B -f /usr/src/app/pom.xml clean package -DbuildFinalName=websocket-speedtest
RUN echo "Test Coverage Total: $(cat /usr/src/app/target/site/jacoco/index.html | grep -oP 'Total.*?\K([0-9]{1,3})%')"

FROM openjdk:8-alpine
COPY --from=build /usr/src/app/target/websocket-speedtest.war /usr/app/ 
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/usr/app/websocket-speedtest.war"] 
