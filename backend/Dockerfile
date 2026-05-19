# syntax=docker/dockerfile:1

# --- Build: fat JAR with Gradle ---
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle/ gradle/

RUN chmod +x gradlew

# Warm dependency cache (layer reused when only source changes)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon -q || true

COPY src/ src/

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew shadowJar --no-daemon -q

# --- Run: local development ---
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
