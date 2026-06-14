# Stage 1: Build the Angular frontend using Node
FROM node:22-alpine AS frontend-builder
WORKDIR /app

# Copy Angular project files
COPY watch-anime-app/package*.json ./
RUN npm install

COPY watch-anime-app/ ./
# Build Angular and output the files directly to a 'static' folder
RUN npm run build -- --output-path=dist/static


# Stage 2: Build the Spring Boot backend using Maven
FROM maven:3.9-eclipse-temurin-25 AS backend-builder
WORKDIR /app

# Copy the Spring Boot project
COPY anime-web/pom.xml .
COPY anime-web/src ./src

# Copy the built Angular files from the Node stage into Spring Boot's static folder
COPY --from=frontend-builder /app/dist/static ./src/main/resources/static

# Build the Spring Boot application
# We skip the frontend-maven-plugin because we already built it in Stage 1!
RUN mvn clean package -DskipTests -Dskip.frontend=true


# Stage 3: Create the final lightweight image
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the executable WAR from the builder stage
COPY --from=backend-builder /app/target/anime-web-0.0.1-SNAPSHOT.war /app/anime-web.war

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/anime-web.war"]
