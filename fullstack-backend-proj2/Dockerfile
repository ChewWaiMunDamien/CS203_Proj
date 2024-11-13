# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim
# Set the working directory inside the container
WORKDIR /app
# Copy the JAR file (produced by Maven) into the container
COPY target/fullstack-backend-proj1-0.0.1-SNAPSHOT.jar myapp.jar
# Expose the port your app will run on
EXPOSE 8080
# Define the command to run your app
ENTRYPOINT ["java", "-jar", "myapp.jar"]