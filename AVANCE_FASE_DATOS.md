# Avance de la fase de datos - MusicPlayerLab

> Documento de seguimiento separado del `README.md` principal.

## Estado actual
- Fase enfocada solo en datos y logica base (sin UI avanzada).
- Arquitectura aplicada en esta etapa: flujo MVVM para `network -> repository -> viewmodel`.
- Integracion de Jamendo API para lectura publica con `client_id` configurable.

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

## Configuracion tecnica aplicada
- Permiso de red agregado en `app/src/main/AndroidManifest.xml`:
  - `android.permission.INTERNET`
- Dependencias minimas ajustadas en `app/build.gradle.kts` y `gradle/libs.versions.toml`:
  - Lifecycle ViewModel
  - Coroutines Android
  - JSON para pruebas unitarias JVM

## Pruebas y validacion ejecutadas
- Build del modulo app:
  - `:app:assembleDebug` -> OK
- Tests unitarios:
  - `testDebugUnitTest` -> OK
- Test agregado para parseo:
  - `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`

## Pendiente inmediato de esta misma linea
- Reemplazar `CLIENT_ID` placeholder por un `client_id` real de Jamendo en:
  - `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`
- Mantener esta fase sin UI avanzada (sin RecyclerView, Adapter, navegacion ni reproductor completo).


