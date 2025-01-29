# Use an OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew gradlew
COPY gradle gradle

# Copy the source code and configuration files
COPY src src
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Grant execute permission to the Gradle wrapper
RUN chmod +x gradlew

# Build the project
RUN ./gradlew bootJar

# Run the application
ENTRYPOINT ["java", "-jar", "build/libs/core-0.0.1-SNAPSHOT.jar"]
