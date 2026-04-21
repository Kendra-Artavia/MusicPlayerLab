# ARQUITECTURA

## Enfoque arquitectónico

`MusicPlayerLab` implementa arquitectura **MVVM** con separación entre:

- Capa de red (`network`)
- Capa de repositorio (`repository`)
- Capa de presentación lógica (`viewmodel`)
- Capa de interfaz (`ui`)

## Flujo principal de datos

Flujo implementado en el proyecto:

`network -> repository -> viewmodel -> ui`

### Detalle del flujo

1. `JamendoApiClient` ejecuta la petición HTTP y retorna `NetworkResult<String>`.
2. `MusicRepository` recibe la respuesta, parsea JSON con `JsonParser` y retorna `NetworkResult<List<Song>>`.
3. `MusicViewModel` transforma resultados en `MusicUiState` y expone `StateFlow`.
4. `MusicPlayerLabApp`/`HomeScreen` observa estado y renderiza componentes Compose.

## Responsabilidades por capa

### Red (`network`)

- `JamendoApiClient`: construcción de URL, conexión HTTP GET, lectura de respuesta, mapeo de errores HTTP y de I/O.
- `JsonParser`: transformación de JSON de Jamendo a modelo de dominio (`Song`).

### Repositorio (`repository`)

- `MusicRepository`: punto de acceso de datos remotos para la UI.
- Valida conectividad con `NetworkConnectivityChecker`.
- Ejecuta llamadas de red y parseo fuera del hilo principal.
- Normaliza errores para consumo del `ViewModel`.
- `FavoritesRepository`: persistencia local de IDs favoritos en `SharedPreferences`.

### ViewModel (`viewmodel`)

- `MusicViewModel` centraliza lógica de presentación:
  - carga inicial
  - búsqueda
  - filtros
  - favoritos
  - estado de preview
- Expone estados reactivos con `StateFlow`.
- Mantiene cache de resultados iniciales para restaurar contenido.

### UI (`ui`)

- `MusicPlayerLabApp` conecta la UI con el `ViewModel`.
- `HomeScreen` organiza layout y acciones del usuario.
- `MusicStateViews` renderiza estados (`Loading`, `Empty`, `Error`, `Success`).
- `HomeSearchBar` gestiona entrada de búsqueda y filtros.

## Clases principales existentes

- `MainActivity`
- `MusicPlayerLabApp` (`HomeScreen.kt`)
- `MusicViewModel`
- `MusicRepository`
- `FavoritesRepository`
- `JamendoApiClient`
- `JsonParser`
- `PreviewPlayerManager`
- `MusicUiState`, `NetworkResult`, `ErrorType`

## Separación entre lógica y UI

La UI se mantiene declarativa y orientada a renderizado. La lógica de negocio y acceso a datos está concentrada en `MusicViewModel`, `MusicRepository` y `JamendoApiClient`. Esta separación facilita mantenimiento, pruebas unitarias y evolución por capas.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)

