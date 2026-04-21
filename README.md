# MusicPlayerLab

Aplicación Android de laboratorio para consultar canciones desde Jamendo, visualizar resultados en una interfaz con Jetpack Compose y gestionar reproducción de preview y favoritos.

## Objetivo del laboratorio

Construir una app móvil con arquitectura por capas (MVVM), consumo HTTP real, parseo de datos, gestión de estados de UI y uso de corrutinas sin bloquear la interfaz.

## Tecnologías principales

- Kotlin
- Android SDK (minSdk 24, targetSdk 36)
- Jetpack Compose + Material 3
- Android ViewModel + `viewModelScope`
- Kotlin Coroutines (`Dispatchers.IO`)
- `HttpURLConnection` para peticiones HTTP GET
- `org.json` para parseo JSON
- JUnit4 para pruebas unitarias

## Índice de documentación

### Punto de entrada técnico

- [`docs/README_TECNICO.md`](docs/README_TECNICO.md)

### Documentación de esta fase

- [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md)
- [`docs/RED_Y_HTTP.md`](docs/RED_Y_HTTP.md)
- [`docs/MULTITAREA.md`](docs/MULTITAREA.md)


## Estado actual del proyecto

- Se implementa una pantalla principal (`MusicPlayerLabApp`/`HomeScreen`) con búsqueda, filtros y estados de carga.
- La capa de datos consume Jamendo mediante `HttpURLConnection` y parsea la respuesta a `Song`.
- El `MusicViewModel` coordina estados de UI, búsquedas, caché inicial y manejo de errores.
- Se incluye gestión de favoritos persistidos en `SharedPreferences`.
- Se implementa reproducción de preview con `MediaPlayer` (play, pause, resume y stop).
- Existen pruebas unitarias para cliente HTTP, parser JSON y repositorio.

## Documentación técnica

Para el detalle técnico del laboratorio, iniciar en:

- [`docs/README_TECNICO.md`](docs/README_TECNICO.md)

