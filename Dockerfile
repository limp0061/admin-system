FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/*-SNAPSHOT.jar admin-system.jar

COPY sql/ /app/sql/

ENTRYPOINT ["java", "-jar", "admin-system.jar"]

