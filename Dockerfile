# syntax=docker/dockerfile:1.2

FROM gradle:8.7-jdk21-alpine AS build

WORKDIR /app

RUN --mount=type=secret,id=app_env \
    mkdir -p /app && \
    cat /run/secrets/app_env > /app/.env

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon || true

COPY . .
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]