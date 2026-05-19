# LTI

LTI is a startup that connects job candidates with companies, building the next-generation **Applicant Tracking System (ATS)**.

This monorepo contains the **Android client** and **backend API** for the project.

## Technologies

### Backend (`backend/`)

| Area | Technology |
|------|------------|
| Language / runtime | Kotlin **1.9.24**, JVM **17** |
| Web framework | [Ktor](https://ktor.io/) **2.3.12** (Netty, routing, content negotiation) |
| JSON | `kotlinx-serialization` (via Ktor) |
| Data layer | [Exposed](https://github.com/JetBrains/Exposed) **0.50.1** (JDBC) |
| Connection pool | [HikariCP](https://github.com/brettwooldridge/HikariCP) **5.1.0** |
| Database | PostgreSQL **16** (driver **42.7.4**) |
| Configuration | HOCON (`application.conf`) + [Typesafe Config](https://github.com/lightbend/config) |
| Logging | Logback **1.4.14** |
| Packaging | [Shadow](https://github.com/johnrengelman/shadow) **8.1.1** (fat JAR) |
| Container | Docker (multi-stage: Gradle + Temurin **17**) |

### Android (`android/`)

| Area | Technology |
|------|------------|
| Language | Kotlin **2.0.21** |
| UI | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3, Compose BOM **2024.12.01**) |
| Architecture | ViewModel + Compose state |
| Networking | [Retrofit](https://square.github.io/retrofit/) **2.11.0**, OkHttp **4.12.0**, `kotlinx-serialization` |
| SDK | `minSdk` **26**, `compileSdk` / `targetSdk` **34** |
| Build | Android Gradle Plugin **8.7.3**, JDK **17** |

### Infrastructure

- **Docker Compose** — PostgreSQL **16-alpine** + backend service
- Ports: **5432** (DB), **8080** (API)

More detail: [backend/README.md](backend/README.md) · [android/README.md](android/README.md)

## Repository structure

```text
lti/
├── android/                    # Android app (Kotlin + Jetpack Compose)
│   ├── app/
│   │   └── src/main/kotlin/com/lti/
│   │       ├── MainActivity.kt
│   │       ├── data/
│   │       │   ├── api/        # Retrofit client & ApiService
│   │       │   └── model/      # DTOs (e.g. HealthResponse)
│   │       └── ui/
│   │           ├── screen/     # Compose screens
│   │           ├── theme/      # Colors, typography, theme
│   │           └── viewmodel/  # ViewModels
│   ├── local.properties.example
│   └── README.md
├── backend/                    # Ktor HTTP API
│   ├── src/main/kotlin/com/lti/
│   │   ├── Application.kt      # Server entry, routes (/health)
│   │   └── db/
│   │       └── DatabaseFactory.kt
│   ├── src/main/resources/application.conf
│   ├── Dockerfile
│   └── README.md
└── docker-compose.yml          # db + backend services
```

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose (for backend + database)
- **JDK 17+**
- For the Android app: [Android SDK](https://developer.android.com/studio) (platform 34), USB debugging on a physical device, Mac and phone on the **same Wi‑Fi** (for local API access)

## Installation & run

### 1. Clone and start backend + database

From the repository root:

```bash
docker compose up --build
```

- PostgreSQL: `localhost:5432` — database `lti_db`, user `lti_user`
- API: **http://localhost:8080** — try `GET /health` → `{"status":"ok"}`

To run only the database and start the backend with Gradle locally, see [backend/README.md](backend/README.md).

### 2. Configure the Android app

Copy the example config and set your machine's LAN IP (where Ktor listens on port 8080):

```bash
cp android/local.properties.example android/local.properties
```

Edit `android/local.properties`:

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
api.base.url=http://YOUR_MAC_LAN_IP:8080/
```

Find your Mac's IP:

```bash
ipconfig getifaddr en0
```

Verify from the phone's browser: `http://<YOUR_MAC_LAN_IP>:8080/health`.

### 3. Build and install the Android app

```bash
cd android
./gradlew installDebug
adb shell am start -n com.lti/.MainActivity
```

The debug build allows cleartext HTTP to your local backend. Use HTTPS for production releases.

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/health` | Service health check (`{"status":"ok"}`) |

## Further reading

- [backend/README.md](backend/README.md) — Gradle run, Shadow JAR, env vars (`DATABASE_JDBC_URL`)
- [android/README.md](android/README.md) — Gradle cache tips, `adb logcat`, device troubleshooting
