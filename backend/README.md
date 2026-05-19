# LTI — Backend

HTTP API for the LTI project, built with **Ktor** and **Exposed** on **PostgreSQL**.

## Technologies

| Area | Technology |
|------|------------|
| Language / runtime | Kotlin **1.9.24**, JVM **17** |
| Web framework | [Ktor](https://ktor.io/) **2.3.12** (Netty, routing, content negotiation) |
| JSON serialization | `kotlinx-serialization` (via Ktor) |
| Data layer | [Exposed](https://github.com/JetBrains/Exposed) **0.50.1** (JDBC) |
| Connection pool | [HikariCP](https://github.com/brettwooldridge/HikariCP) **5.1.0** |
| Database | PostgreSQL (driver **42.7.4**) |
| Configuration | HOCON (`application.conf`) with [Typesafe Config](https://github.com/lightbend/config) |
| Logging | Logback **1.4.14** |
| Packaging | [Shadow](https://github.com/johnrengelman/shadow) **8.1.1** (fat JAR / `-all.jar`) |
| Container | Docker (multi-stage: Gradle + Temurin **17**) |

## File layout

```text
backend/
├── build.gradle.kts          # Dependencies, plugins, mainClass, Shadow
├── settings.gradle.kts       # Gradle project name (lti-backend)
├── gradle.properties
├── gradlew / gradlew.bat     # Gradle wrapper
├── gradle/wrapper/           # Wrapper version
├── Dockerfile                # Production image (shadowJar + Java 17)
├── .dockerignore
└── src/main/
    ├── kotlin/com/lti/
    │   ├── Application.kt    # main(), Netty server, plugins, routes (/health)
    │   └── db/
    │       └── DatabaseFactory.kt   # HikariCP, Exposed, SELECT 1 check
    └── resources/
        └── application.conf  # jdbcUrl, driver, user, password (overridable)
```

From the monorepo root, `docker-compose.yml` defines the `db` (PostgreSQL) and `backend` (this project) services.

## How to run

### Option A — Docker Compose (recommended)

From the **repository root** (`lti/`), with Docker installed:

```bash
docker compose up --build
```

- PostgreSQL is exposed on port **5432** (user `lti_user`, database `lti_db`; credentials match `application.conf`).
- The backend listens on **http://localhost:8080** (e.g. `GET /health`).

Inside Compose, the JDBC URL is injected via `DATABASE_JDBC_URL` pointing at the `db` service.

### Option B — Local with Gradle

1. Start PostgreSQL (you can run only the database from Compose):

   ```bash
   docker compose up db
   ```

2. From `backend/`, run:

   ```bash
   ./gradlew run
   ```

By default this uses `jdbc:postgresql://localhost:5432/lti_db` and the credentials in `src/main/resources/application.conf`. For a different host or database, set:

```bash
export DATABASE_JDBC_URL="jdbc:postgresql://host:5432/your_database"
./gradlew run
```

(`application.conf` uses optional substitution `${?DATABASE_JDBC_URL}`.)

### Runnable JAR

```bash
./gradlew shadowJar
java -jar build/libs/lti-backend-0.0.1-SNAPSHOT-all.jar
```

(Ensure PostgreSQL is reachable and export `DATABASE_JDBC_URL` if you are not using the defaults in `application.conf`.)

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/health` | Verifies the service responds (`{"status":"ok"}`). The DB connection is initialized at startup and logged. |
