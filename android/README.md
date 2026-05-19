# LTI Android

Android client (Kotlin + Jetpack Compose) for the LTI monorepo.

## Requirements

- JDK 17+
- [Android SDK](https://developer.android.com/studio#command-line-tools-only) (platform 34, build-tools)
- Physical device with USB debugging enabled
- Mac and phone on the **same Wi‑Fi network**

## Local setup

Create or edit `android/local.properties` (not committed to git):

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk

# Your Mac's LAN IP where Ktor runs (:8080)
api.base.url=http://192.168.1.42:8080/
```

Get your Mac's IP address:

```bash
ipconfig getifaddr en0
```

The backend must listen on all interfaces (`0.0.0.0:8080`, already set in Ktor). Start the backend on your Mac before testing the app.

## Build and install

```bash
cd android
./gradlew installDebug
adb shell am start -n com.lti/.MainActivity
```

## Faster Gradle builds

The repo’s `gradle.properties` enables:

- **`org.gradle.daemon=true`** — keeps a JVM warm between runs so Gradle does not cold-start every time.
- **`org.gradle.caching=true`** — **build cache**: reuses task outputs (e.g. compiled classes) when inputs are unchanged. Cached data lives under `~/.gradle/caches/` (including downloaded dependencies).
- **`org.gradle.parallel=true`** — runs independent work in parallel where possible.
- **`kotlin.incremental=true`** — Kotlin incremental compilation for quicker recompiles after small edits.

**Practical tips**

- Avoid `./gradlew clean` unless you really need a full rebuild; clean wipes outputs and forces more work next time.
- Run `./gradlew installDebug` from the same machine so the same `~/.gradle` is reused (that is your “Gradle cache”).
- Optional: set `GRADLE_USER_HOME` to a fixed directory if you ever need to pin where caches live.

First run after a dependency or AGP upgrade can still be slow while artifacts are resolved; later `installDebug` runs should be much faster when only app code changed.

## Verify the backend from your phone

Open `http://<your-mac-ip>:8080/health` in the phone's browser. If you get JSON back, Retrofit can connect too.

## Device logs (`adb logcat`)

With USB debugging on and `adb` on your PATH, connect the phone and run:

```bash
adb logcat
```

Stop with `Ctrl+C`.

**Filter by this app** (`com.lti`). Open the app first, then:

```bash
adb logcat --pid=$(adb shell pidof -s com.lti)
```

**Reduce noise** (warnings and above):

```bash
adb logcat *:W
```

Levels: `V` (verbose) through `E` (error).

**OkHttp / Retrofit** (request/response body when using `HttpLoggingInterceptor`):

```bash
adb logcat OkHttp:D *:S
```

**Multiple devices** — pick one:

```bash
adb devices
adb -s <device_id> logcat
```

In **Android Studio**: **View → Tool Windows → Logcat** (same stream, with UI filters).

## Notes

- The **debug** build allows HTTP (cleartext) to your local backend.
- **release** does not allow cleartext HTTP; use HTTPS in production.
