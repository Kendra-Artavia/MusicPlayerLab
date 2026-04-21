# README_TECNICO

## Resumen técnico general

Este documento presenta la base técnica de `MusicPlayerLab` según el estado real del repositorio. La aplicación sigue una arquitectura MVVM y separa responsabilidades entre red, repositorio, lógica de presentación y UI en Compose.

## Propósito de la app

Permitir la consulta de canciones desde la API de Jamendo, mostrar resultados en una lista, reproducir previews de audio y marcar canciones favoritas de forma persistente.

## Alcance actual

- Búsqueda de canciones por texto.
- Carga inicial de canciones con consulta por defecto (`rock`).
- Visualización de estados de UI: `Idle`, `Loading`, `Success`, `Error`.
- Filtro de canciones (`ALL`, `FAVORITES_ONLY`).
- Favoritos persistidos con `SharedPreferences`.
- Reproducción de preview con `MediaPlayer`.
- Validación de conectividad antes de llamar a red.
- Pruebas unitarias en red y repositorio.

No se documentan funcionalidades no presentes en el código actual (por ejemplo, reproducción en segundo plano, navegación entre múltiples pantallas o caché local de catálogo completo).

## Módulos implementados

### Capa de presentación (UI)

- `app/src/main/java/com/example/musicplayerlab/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/HomeSearchBar.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/MusicStateViews.kt`

### Capa de ViewModel

- `app/src/main/java/com/example/musicplayerlab/viewmodel/MusicViewModel.kt`
- `app/src/main/java/com/example/musicplayerlab/viewmodel/MusicUiState.kt`
- `app/src/main/java/com/example/musicplayerlab/viewmodel/SongListFilter.kt`
- `app/src/main/java/com/example/musicplayerlab/viewmodel/PreviewPlaybackState.kt`

### Capa de datos y red

- `app/src/main/java/com/example/musicplayerlab/repository/MusicRepository.kt`
- `app/src/main/java/com/example/musicplayerlab/repository/FavoritesRepository.kt`
- `app/src/main/java/com/example/musicplayerlab/network/JamendoApiClient.kt`
- `app/src/main/java/com/example/musicplayerlab/network/JsonParser.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkConnectivityChecker.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/NetworkResult.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/ErrorType.kt`
- `app/src/main/java/com/example/musicplayerlab/utils/Constants.kt`

## Relación entre capa de datos y capa de UI

La UI no consume red directamente. El flujo implementado es:

1. La UI invoca acciones del `MusicViewModel` (`loadInitialSongs`, `onSearch`, filtros, favoritos, preview).
2. El `MusicViewModel` solicita datos al `MusicRepository`.
3. El `MusicRepository` valida conectividad, ejecuta el cliente HTTP (`JamendoApiClient`) y parsea JSON (`JsonParser`).
4. El resultado se devuelve como `NetworkResult` y se transforma en `MusicUiState`.
5. La UI observa `StateFlow` y se recompone con el nuevo estado.

## Cumplimiento general del laboratorio

- Se aplica arquitectura por capas con separación de responsabilidades.
- Se implementa consumo HTTP GET real con manejo de errores.
- Se utiliza multitarea con corrutinas y `Dispatchers.IO` en operaciones de red/datos.
- Se mantiene la UI reactiva mediante `StateFlow` y `collectAsStateWithLifecycle`.
- Se cuenta con pruebas unitarias que validan comportamiento de red, parseo y repositorio.

## Enlaces relacionados

- Volver al inicio: [`../README.md`](../README.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)

