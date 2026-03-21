# Builds and runs the BOCRA Spring Boot API as a single container for Render-style deployments.
FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
