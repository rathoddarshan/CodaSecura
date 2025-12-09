# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the build artifact (JAR file) from your local machine to the container
# Note: Ensure you run 'mvn clean package' before building the docker image
COPY target/secure-coda-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 so we can access the dashboard
EXPOSE 8080

# Environment variables (Can be overridden at runtime)
# We set a placeholder for the token so the app doesn't crash on start
ENV CODA_API_TOKEN="Bearer 28eed67a-375c-4fc3-aa03-ec603068032e"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]