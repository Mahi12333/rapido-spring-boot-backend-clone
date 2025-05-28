# Dockerfile
FROM eclipse-temurin:21-jdk-alpine


# Set environment variable for optional Java options
ENV JAVA_OPTS=""

# Maintainer information
LABEL maintainer="Mahitosh Giri <mahitoshgiri287@gmail.com>"

# Set the working directory
WORKDIR /app

# Copy the built JAR (update the JAR name if it's different)
COPY target/Rapido-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 5001

# Run the Spring Boot app
#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]