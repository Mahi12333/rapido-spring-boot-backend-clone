# Dockerfile
FROM eclipse-temurin:21-jdk-alpine

# Maintainer information
LABEL maintainer="Mahitosh Giri <mahitoshgiri287@gmail.com>"

# Set the working directory
WORKDIR /app

# Copy the built JAR (update the JAR name if it's different)
COPY target/Rapido-0.0.1-SNAPSHOT.jar app.jar

# Expose the Spring Boot default port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
