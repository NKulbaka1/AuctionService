# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and dependency descriptors first for layer caching
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Download dependencies (cached unless build.gradle changes)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q

# Copy source and build the fat jar
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: run ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
