# Stage 1: Build with Maven 3.9.11 + Java 21
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only the necessary files to download dependencies first
COPY pom.xml .
COPY src ./src

# Build the app (skip tests if not needed)
RUN mvn clean package -DskipTests

# Stage 2: Run with JRE only
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app listens on (Render sets $PORT)
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
