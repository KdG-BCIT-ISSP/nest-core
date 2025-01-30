FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY src src
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

RUN chmod +x gradlew

RUN ./gradlew bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/core-0.0.1-SNAPSHOT.jar app.jar

ARG POSTGRES_URL
ARG POSTGRES_USERNAME
ARG POSTGRES_PASSWORD
ARG POSTGRES_DB_PLATFORM
ARG JWT_SECRET

ENV POSTGRES_URL=$POSTGRES_URL
ENV POSTGRES_USERNAME=$POSTGRES_USERNAME
ENV POSTGRES_PASSWORD=$POSTGRES_PASSWORD
ENV POSTGRES_DB_PLATFORM=$POSTGRES_DB_PLATFORM
ENV JWT_SECRET=$JWT_SECRET

ENTRYPOINT ["java", "-jar", "app.jar"]
