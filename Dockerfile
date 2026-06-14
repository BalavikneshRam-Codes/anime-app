# Stage 1: Build the entire application (Frontend + Backend) using Maven
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app

# Copy the frontend project
COPY watch-anime-app/ ./watch-anime-app/

# Copy the backend project
COPY anime-web/ ./anime-web/

# Build the project
WORKDIR /app/anime-web

# CRITICAL FIX: Delete the old, pre-compiled frontend files that were accidentally 
# committed to GitHub so they don't override our fresh build!
RUN rm -rf src/main/webapp/*

# Run Maven (this will automatically download Node, build Angular, and package the WAR)
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the executable WAR from the builder stage
COPY --from=builder /app/anime-web/target/anime-web-0.0.1-SNAPSHOT.war /app/anime-web.war

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/anime-web.war"]
