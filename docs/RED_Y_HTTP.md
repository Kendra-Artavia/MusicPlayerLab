# RED_Y_HTTP

## Alcance de este documento

Este documento describe el estado actual de la comunicación de red del proyecto con Jamendo usando `HttpURLConnection`.

## Cliente HTTP implementado

Archivo principal:

- `app/src/main/java/com/example/musicplayerlab/network/JamendoApiClient.kt`

La clase `JamendoApiClient` ejecuta búsquedas remotas mediante `searchSongsByText` y `searchTracks`.

## Uso de `HttpURLConnection`

El cliente crea conexiones con:

- `URL(url).openConnection() as HttpURLConnection`

Y configura explícitamente:

- `requestMethod = "GET"`
- `doInput = true`
- header `Accept: application/json`

## Método GET y URL de consulta

La URL se construye con:

- `Constants.BASE_URL = https://api.jamendo.com/v3.0`
- endpoint `tracks`
- parámetros `client_id`, `format`, `limit`, `namesearch`

El parámetro de búsqueda usa `URLEncoder` para codificar texto de forma segura.

## Configuración de conexión y timeouts

Configuración actual en `Constants.kt`:

- `CONNECTION_TIMEOUT_MS = 15_000`
- `READ_TIMEOUT_MS = 15_000`

Se aplican en `JamendoApiClient.createConnection` mediante `connectTimeout` y `readTimeout`.

## Manejo de códigos de respuesta HTTP

La lógica implementada clasifica respuestas:

- `200 OK` + body no vacío -> `NetworkResult.Success`
- `200 OK` + body vacío -> `ErrorType.EMPTY_RESPONSE_BODY`
- `4xx` -> `ErrorType.HTTP_CLIENT`
- `5xx` -> `ErrorType.HTTP_SERVER`
- otro código -> `ErrorType.HTTP_UNEXPECTED`

Además, captura excepciones:

- `SocketTimeoutException` -> `ErrorType.TIMEOUT`
- `IOException` -> `ErrorType.NETWORK_IO`
- otra excepción -> `ErrorType.UNKNOWN`

## Consumo de Jamendo API

El flujo real es:

1. `MusicRepository` solicita `apiClient.searchSongsByText(query, limit)`.
2. `JamendoApiClient` retorna texto JSON (`NetworkResult<String>`).
3. `JsonParser.parseTrackResponse` transforma JSON a `List<Song>`.
4. `MusicRepository` retorna `NetworkResult<List<Song>>` al `ViewModel`.

## Validación de conectividad

Antes de llamar a red, `MusicRepository` valida internet con `NetworkConnectivityChecker`:

- implementación Android: `AndroidNetworkConnectivityChecker`
- criterio: `NET_CAPABILITY_INTERNET` y `NET_CAPABILITY_VALIDATED`

Si no hay conectividad, retorna `ErrorType.NO_INTERNET` sin ejecutar petición HTTP.

## Relación con repository y parser

- `MusicRepository` orquesta conectividad, red, parseo y normalización de errores.
- `JsonParser` encapsula el mapeo de JSON a dominio (`Song`).
- `MusicViewModel` consume resultados y los convierte en estado de UI.

## Evidencia en pruebas unitarias

- `app/src/test/java/com/example/musicplayerlab/network/JamendoApiClientTest.kt`
- `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`
- `app/src/test/java/com/example/musicplayerlab/repository/MusicRepositoryTest.kt`

Estas pruebas cubren validación de query, URL, códigos HTTP, timeouts, errores de red y parseo.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)

