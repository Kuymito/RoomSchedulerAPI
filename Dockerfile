# Stage 1: Build the application with Maven
# Use a Maven image that includes OpenJDK 21 to match the project's requirement
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and project definition files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Add execute permission to the Maven wrapper to prevent build errors
RUN chmod +x ./mvnw

# Download dependencies to leverage Docker's layer caching
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Package the application into a JAR file, skipping tests
RUN ./mvnw package -DskipTests

# Stage 2: Create the final, lightweight production image
# Use a minimal and reliable Java 21 runtime image
FROM eclipse-temurin:21-jre as final

WORKDIR /app

# Copy the executable JAR from the build stage to the final image
COPY --from=build /app/target/RoomSchedulerAPI-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# The command to run your application
CMD ["java", "-jar", "app.jar"]