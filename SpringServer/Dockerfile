FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/server-0.0.1-SNAPSHOT.war app.jar

ENTRYPOINT ["java","-jar","app.jar"]

EXPOSE 8090
