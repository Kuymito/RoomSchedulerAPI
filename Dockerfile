# Stage 1: Build the application with Maven
# Use a Maven image that includes OpenJDK 21 to match the project's requirement
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and project definition files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Add execute permission to the Maven wrapper
RUN chmod +x ./mvnw

# Download dependencies. This is done as a separate step to leverage Docker's layer caching.
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Build the application, creating the executable JAR file.
# We skip tests to make the cloud build faster.
RUN ./mvnw spring-boot:build-image -DskipTests

# Stage 2: Create the final, lightweight production image
# We use an official OpenJDK image which is smaller than the Maven image
FROM springboot/build-image

# Expose the port that your Spring Boot application runs on (default is 8080)
EXPOSE 8080

# The command to run your application
# The JAR file is automatically located in the /workspace directory by the build-image command
CMD ["java", "-jar", "/workspace/application.jar"]