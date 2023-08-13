# syntax=docker/dockerfile:1

FROM eclipse-temurin:20-jdk-jammy

WORKDIR /app

ARG GITHUB_TOKEN

ENV GITHUB_TOKEN=${GITHUB_TOKEN}

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

EXPOSE 8080:8080

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]