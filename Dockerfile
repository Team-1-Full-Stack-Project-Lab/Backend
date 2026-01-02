# ========================================
# Stage 1: Build
# ========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install build dependencies
RUN apk add --no-cache bash

WORKDIR /app

# Copy Gradle wrapper and configuration files first for better layer caching
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew

# Download dependencies (separate layer for caching)
RUN ./gradlew --no-daemon dependencies || true

# Copy source code
COPY src ./src

# Build the application (skip tests in Docker build, run them in CI/CD)
RUN ./gradlew --no-daemon clean bootJar -x test

# Verify JAR was created
RUN ls -lh /app/build/libs/

# ========================================
# Stage 2: Runtime
# ========================================
FROM eclipse-temurin:21-jre-alpine

# Add metadata labels
LABEL maintainer="Team 1"
LABEL description="Full Stack Project Lab - Backend"
LABEL version="1.0"

# Create non-root user for security
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Run the application
ENTRYPOINT exec java $JAVA_OPTS -jar /app/app.jar
