# Multi-stage build: compile the WAR with Maven, then run it on Tomcat 9.
# Matches the spec's tech stack (JDK 17, Tomcat 9) without needing any
# manual server setup on the hosting platform's side.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk17-temurin
# Remove Tomcat's default sample apps to keep the image lean and avoid
# any path confusion with our own app.
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/yogamart.war /usr/local/tomcat/webapps/yogamart.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
