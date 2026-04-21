# AVANCE_FASE_DATOS

## Propósito del documento

Este documento consolida el avance de la fase de datos de `MusicPlayerLab` y lo alinea con la documentación técnica actual en `docs/`.

## Estado actual de la fase de datos

- Arquitectura de datos activa: `network -> repository -> viewmodel`.
- Integración de Jamendo mediante `HttpURLConnection` y método GET.
- Parseo JSON implementado y validado con pruebas unitarias.
- Verificación de conectividad de red previa al consumo HTTP.
- Manejo tipado de errores con `NetworkResult` y `ErrorType`.

## Componentes implementados en datos

### Modelos

- `app/src/main/java/com/example/musicplayerlab/model/Song.kt`
- `app/src/main/java/com/example/musicplayerlab/model/JamendoTrackResponse.kt`

### Red

- `app/src/main/java/com/example/musicplayerlab/network/JamendoApiClient.kt`
- `app/src/main/java/com/example/musicplayerlab/network/JsonParser.kt`

### Repositorio y utilidades

- `app/src/main/java/com/example/musicplayerlab/repository/MusicRepository.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkConnectivityChecker.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkResult.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/ErrorType.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`

## Decisiones técnicas aplicadas

- `client_id` de Jamendo no hardcodeado en código fuente:
  - se toma desde `local.properties` o propiedad Gradle,
  - se expone en `BuildConfig`.
- Timeouts de conexión y lectura definidos en constantes.
- Validación de conectividad en repositorio para evitar llamadas remotas sin internet.
- Parseo robusto con campos opcionales y fallback (`audio` -> `audiodownload`).

## Errores considerados en la fase

El flujo contempla, entre otros:

- query vacía,
- `client_id` ausente,
- body HTTP vacío,
- errores HTTP cliente/servidor,
- timeout,
- error de red I/O,
- falta de internet,
- fallo de parseo,
- error desconocido.

## Validación y pruebas

Pruebas unitarias existentes:

- `app/src/test/java/com/example/musicplayerlab/network/JamendoApiClientTest.kt`
- `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`
- `app/src/test/java/com/example/musicplayerlab/repository/MusicRepositoryTest.kt`

Cobertura funcional de la fase:

- construcción de URL de Jamendo,
- clasificación de errores HTTP,
- mapeo JSON a `Song`,
- propagación correcta de errores desde red a repositorio.

## Coherencia con la capa de UI

La capa de datos entrega resultados tipados al `MusicViewModel`, que transforma dichos resultados en `MusicUiState` para la UI Compose.

## Alcance y límites

Este avance documenta exclusivamente la fase de datos y su integración con capas superiores. Los detalles de interfaz se documentan en `docs/AVANCE_FASE_UI.md`.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Procesamiento JSON: [`PROCESAMIENTO_JSON.md`](PROCESAMIENTO_JSON.md)
- Componente del dispositivo: [`COMPONENTE_DISPOSITIVO.md`](COMPONENTE_DISPOSITIVO.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- UI Compose: [`UI_COMPOSE.md`](UI_COMPOSE.md)
- Ejecución y pruebas: [`EJECUCION_Y_PRUEBAS.md`](EJECUCION_Y_PRUEBAS.md)
- Avance fase UI: [`AVANCE_FASE_UI.md`](AVANCE_FASE_UI.md)

