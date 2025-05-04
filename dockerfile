# Use an official OpenJDK runtime as the base image
FROM amazoncorretto:23-alpine

# Set the working directory inside the container
WORKDIR /app

RUN apk add bash
# Copy the Gradle wrapper and build files first (for caching)
COPY gradlew gradlew.bat gradle/ /app/
COPY build.gradle.kts settings.gradle.kts /app/
COPY ./gradle /app/gradle
RUN ls
# Grant execute permission for Gradle wrapper
RUN chmod +x gradlew
RUN ./gradlew
# Copy the source code
COPY src/ /app/src/
# ENTRYPOINT bash

# Build the application inside the container
RUN ./gradlew build -x test

# Find and copy the generated JAR file
RUN cp build/libs/*.jar app.jar

# COPY build/libs/DistributedSystems-1.0-SNAPSHOT.jar app.jar

# Expose the port your application runs on
# EXPOSE 8080

# Run the application
ENTRYPOINT ["./gradlew","run"]
