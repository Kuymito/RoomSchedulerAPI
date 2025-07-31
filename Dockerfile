# Use a Maven image with Java 21 to build the application
FROM maven:3.9-eclipse-temurin-21 as builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application, skipping tests for a faster build
RUN mvn package -DskipTests

# Use a lightweight Java 21 JRE image for the final container
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/RoomSchedulerAPI-0.0.1-SNAPSHOT.jar /app.jar

# Expose the port the app runs on (Cloud Run provides the $PORT variable, defaulting to 8080)
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]