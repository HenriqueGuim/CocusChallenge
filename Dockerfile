# syntax=docker/dockerfile:1

FROM eclipse-temurin:20-jdk-jammy

WORKDIR /app

ENV GITHUB_TOKEN=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

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

EXPOSE 8080:8080

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]