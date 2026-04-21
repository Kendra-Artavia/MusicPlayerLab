# Avance de la fase de datos - MusicPlayerLab

> Documento de seguimiento separado del `README.md` principal.

## Estado actual
- Fase enfocada solo en datos y logica base (sin UI avanzada).
- Arquitectura aplicada en esta etapa: flujo MVVM para `network -> repository -> viewmodel`.
- Integracion de Jamendo API para lectura publica con `client_id` configurable.

## Reglas generales de esta fase
- Todo el codigo fuente se mantiene en ingles (nombres, tipos, mensajes tecnicos internos).
- Todo texto visible al usuario se centraliza en `app/src/main/res/values/strings.xml`.
- No se usan textos hardcodeados para UI.
- En Compose se debe consumir texto solo con `stringResource(...)`.
- MVVM se mantiene limpio: capa de datos expone errores tipados y la capa de presentacion resuelve el mensaje localizado.

## Paquetes creados
- `app/src/main/java/com/example/musicplayerlab/model`
- `app/src/main/java/com/example/musicplayerlab/network`
- `app/src/main/java/com/example/musicplayerlab/repository`
- `app/src/main/java/com/example/musicplayerlab/viewmodel`
- `app/src/main/java/com/example/musicplayerlab/utils`

## Clases implementadas
### model
- `app/src/main/java/com/example/musicplayerlab/model/Song.kt`
- `app/src/main/java/com/example/musicplayerlab/model/JamendoTrackResponse.kt`

### network
- `app/src/main/java/com/example/musicplayerlab/network/JamendoApiClient.kt`
- `app/src/main/java/com/example/musicplayerlab/network/JsonParser.kt`

### repository
- `app/src/main/java/com/example/musicplayerlab/repository/MusicRepository.kt`

### viewmodel
- `app/src/main/java/com/example/musicplayerlab/viewmodel/MusicUiState.kt`
- `app/src/main/java/com/example/musicplayerlab/viewmodel/MusicViewModel.kt`

### utils
- `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkResult.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkConnectivityChecker.kt`

## Configuracion tecnica aplicada
- Permiso de red agregado en `app/src/main/AndroidManifest.xml`:
  - `android.permission.INTERNET`
  - `android.permission.ACCESS_NETWORK_STATE`
- Dependencias minimas ajustadas en `app/build.gradle.kts` y `gradle/libs.versions.toml`:
  - Lifecycle ViewModel
  - Coroutines Android
  - JSON para pruebas unitarias JVM
- `client_id` de Jamendo configurado de forma no hardcodeada:
  - `local.properties` (`JAMENDO_CLIENT_ID`)
  - expuesto por `BuildConfig` en `app/build.gradle.kts`
  - consumido en `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`
- Validacion de conectividad integrada en la capa de datos:
  - `MusicRepository` evita llamada HTTP si no hay internet

## Pruebas y validacion ejecutadas
- Build del modulo app:
  - `:app:assembleDebug` -> OK
- Tests unitarios:
  - `:app:testDebugUnitTest` -> OK
- Tests agregados para parseo y red:
  - `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`
  - `app/src/test/java/com/example/musicplayerlab/network/JamendoApiClientTest.kt`
  - `app/src/test/java/com/example/musicplayerlab/repository/MusicRepositoryTest.kt`

## Pendiente inmediato de esta misma linea
- No hay pendientes bloqueantes en fase de datos/logica base.
- Pendiente menor (opcional): agregar pruebas unitarias de `MusicViewModel` para transiciones de estado (`Idle`, `Loading`, `Success`, `Error`).
- Mantener esta fase sin UI avanzada (sin RecyclerView, Adapter, navegacion ni reproductor completo).

## Nota de separacion de seguimiento
- El avance de la interfaz grafica Compose se movio a `AVANCE_FASE_UI.md`.
- Este documento queda enfocado solo en fase de datos y logica base.


