# ---- Build ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Gradle wrapper y metadatos primero (mejor caché)
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew

# (opcional) descarga de dependencias en capa caché
RUN ./gradlew --no-daemon dependencies || true

# Código fuente
COPY src ./src

# Empaquetar jar
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Run ----
# (para evitar dudas con tags JRE, usamos JDK también en runtime)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
