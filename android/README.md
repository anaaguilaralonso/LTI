# LTI Android

Cliente Android (Kotlin + Jetpack Compose) del monorepo LTI.

## Requisitos

- JDK 17+
- [Android SDK](https://developer.android.com/studio#command-line-tools-only) (platform 34, build-tools)
- Variable de entorno `ANDROID_HOME` (o `ANDROID_SDK_ROOT`) apuntando al SDK

`local.properties` se genera automáticamente si el SDK está en la ruta por defecto de macOS/Linux; si no, créalo:

```properties
sdk.dir=/ruta/a/tu/Android/sdk
```

## Compilar

```bash
cd android
./gradlew assembleDebug
```

APK de salida: `app/build/outputs/apk/debug/app-debug.apk`

## Instalar en dispositivo o emulador

Con `adb` en el PATH y un dispositivo conectado:

```bash
./gradlew installDebug
```

O manualmente:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Ejecutar sin Android Studio

1. Crea un AVD con `avdmanager` / `sdkmanager` o usa un dispositivo físico con depuración USB.
2. Inicia el emulador: `emulator -avd <nombre>`
3. `./gradlew installDebug` y abre la app «LTI» en el launcher.
