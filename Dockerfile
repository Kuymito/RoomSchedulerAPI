# Use a Maven image to build the application
FROM maven:3.8-jdk-11 as builder

# Copy the project files and build the application
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Use a slim OpenJDK image for the final container
FROM adoptopenjdk/openjdk11:alpine-slim

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/RoomSchedulerAPI-0.0.1-SNAPSHOT.jar /app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java","-jar","/app.jar"]