FROM maven:3-jdk-8-alpine AS build  
COPY src /usr/src/app/src 
COPY pom.xml /usr/src/app 
RUN mvn -B -f /usr/src/app/pom.xml clean package -DbuildFinalName=websocket-speedtest

FROM openjdk:8-alpine
COPY --from=build /usr/src/app/target/websocket-speedtest.war /usr/app/ 
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/usr/app/websocket-speedtest.war"] 
