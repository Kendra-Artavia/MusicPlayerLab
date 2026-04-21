# EJECUCION_Y_PRUEBAS

## Requisitos mínimos

- Windows, Linux o macOS con entorno Android válido
- JDK 11 (alineado con `sourceCompatibility` y `targetCompatibility` del módulo `app`)
- Android SDK con API de compilación configurada por Gradle
- Android Studio o herramientas de línea de comandos de Android

## Configuración de `JAMENDO_CLIENT_ID`

La app obtiene `JAMENDO_CLIENT_ID` desde:

1. `local.properties` (clave `JAMENDO_CLIENT_ID`), o
2. propiedad Gradle `JAMENDO_CLIENT_ID`.

Configuración recomendada en `local.properties` (raíz del proyecto):

```properties
JAMENDO_CLIENT_ID=tu_client_id
```

La propiedad se inyecta en `BuildConfig` en:

- `app/build.gradle.kts`

Y se consume en:

- `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`

## Compilación

Desde la raíz del proyecto (`C:\Universidad\MusicPlayerLab`), comandos Gradle habituales:

```powershell
.\gradlew.bat :app:assembleDebug
```

Para compilar todos los módulos/tareas por defecto:

```powershell
.\gradlew.bat build
```

## Ejecución

Opciones de ejecución:

1. Desde Android Studio: ejecutar configuración del módulo `app` en emulador/dispositivo.
2. Desde línea de comandos: generar APK debug e instalarlo con flujo Android estándar.

Comando para generar APK debug:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Pruebas existentes

### Unit tests (JVM)

Ubicación:

- `app/src/test/java/com/example/musicplayerlab/network/JamendoApiClientTest.kt`
- `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`
- `app/src/test/java/com/example/musicplayerlab/repository/MusicRepositoryTest.kt`

Cobertura actual de pruebas:

- validación de query y `client_id`
- construcción de URL y codificación de búsqueda
- clasificación de errores HTTP (`4xx`, `5xx`, inesperados)
- timeout e I/O
- parseo JSON y fallback de `audiodownload`
- propagación de errores y comportamiento del repositorio

### Instrumentation test base

Ubicación:

- `app/src/androidTest/java/com/example/musicplayerlab/ExampleInstrumentedTest.kt`

## Comandos Gradle relevantes

Ejecución de tests unitarios del módulo `app`:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Ejecución de instrumentation tests (requiere dispositivo/emulador):

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Limpieza de artefactos:

```powershell
.\gradlew.bat clean
```

## Relación con el laboratorio

La ejecución y validación actuales demuestran:

- compilación del módulo Android,
- integración de configuración externa segura para `client_id`,
- verificación de la capa de datos con pruebas unitarias reproducibles.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Procesamiento JSON: [`PROCESAMIENTO_JSON.md`](PROCESAMIENTO_JSON.md)
- Componente del dispositivo: [`COMPONENTE_DISPOSITIVO.md`](COMPONENTE_DISPOSITIVO.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- UI Compose: [`UI_COMPOSE.md`](UI_COMPOSE.md)
- Avance fase datos: [`AVANCE_FASE_DATOS.md`](AVANCE_FASE_DATOS.md)
- Avance fase UI: [`AVANCE_FASE_UI.md`](AVANCE_FASE_UI.md)

