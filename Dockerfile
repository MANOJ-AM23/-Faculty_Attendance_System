# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy both frontend and backend
COPY frontend ./frontend
COPY backend ./backend

# Build the Spring Boot application
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Maintain the exact folder structure needed by application.properties `file:../frontend/`
COPY --from=build /app/frontend /app/frontend
COPY --from=build /app/backend/target/faculty-attendance-1.0.0.jar /app/backend/app.jar

# Expose the Railway expected port
EXPOSE 8080

# Switch to the backend directory where the app should execute from
WORKDIR /app/backend

# Run the jar with production profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod", "--server.port=${PORT:8080}"]
