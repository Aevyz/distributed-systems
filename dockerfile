FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle clean shadowJar --no-daemon

# Create a lightweight image with only the JAR
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# This code has been generated with the assistance of ChatGPT
