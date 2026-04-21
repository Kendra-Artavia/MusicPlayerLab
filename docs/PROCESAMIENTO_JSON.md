# PROCESAMIENTO_JSON

## Propósito

Este documento describe el procesamiento JSON implementado en `MusicPlayerLab` para transformar la respuesta de Jamendo en modelos de dominio consumidos por la UI.

## Estructura JSON recibida desde Jamendo

La app consume el endpoint `tracks` de Jamendo y procesa principalmente el arreglo `results`.

Estructura relevante (simplificada):

```json
{
  "results": [
    {
      "id": 123,
      "name": "Nombre de canción",
      "artist_name": "Nombre de artista",
      "audio": "https://...",
      "audiodownload": "https://...",
      "image": "https://...",
      "duration": 240,
      "album_name": "Nombre de álbum"
    }
  ]
}
```

## Parsing actual

El parseo se implementa en:

- `app/src/main/java/com/example/musicplayerlab/network/JsonParser.kt`

Lógica principal:

1. Se crea `JSONObject` con el body completo.
2. Se obtiene `results` con `optJSONArray("results")`.
3. Se recorre cada elemento y se convierte con `toSong()`.
4. Se retorna `JamendoTrackResponse(results = songs)`.

Si `results` no existe, el parser utiliza un `JSONArray` vacío y devuelve una lista vacía.

## Clases de modelo relacionadas

Modelos usados en la transformación:

- `app/src/main/java/com/example/musicplayerlab/model/JamendoTrackResponse.kt`
- `app/src/main/java/com/example/musicplayerlab/model/Song.kt`

`JamendoTrackResponse` contiene:

- `results: List<Song>`

`Song` contiene:

- `id: String`
- `title: String`
- `artistName: String`
- `audioUrl: String`
- `imageUrl: String?`
- `durationSeconds: Int?`
- `albumName: String?`

## Transformación a `Song`

Mapeo implementado en `JsonParser.toSong()`:

- `id` <- `readString("id")` (acepta `String` o `Number`)
- `title` <- `optString("name")`
- `artistName` <- `optString("artist_name")`
- `audioUrl` <- `optString("audio")`, con fallback a `optString("audiodownload")`
- `imageUrl` <- `optString("image")` si no está en blanco
- `durationSeconds` <- `optInt("duration")` si el campo existe y no es nulo
- `albumName` <- `optString("album_name")` si no está en blanco

## Manejo de errores de parseo

El manejo de error no se realiza dentro de `JsonParser` como `try/catch` local; se resuelve en repositorio:

- `MusicRepository` invoca `JsonParser.parseTrackResponse(...)` dentro de un bloque `try`.
- Si ocurre excepción durante parseo, retorna:
  - `NetworkResult.Error(type = ErrorType.PARSE_FAILURE, throwable = exception)`

Archivo de referencia:

- `app/src/main/java/com/example/musicplayerlab/repository/MusicRepository.kt`

## Evidencia en pruebas

Pruebas unitarias relacionadas:

- `app/src/test/java/com/example/musicplayerlab/network/JsonParserTest.kt`
- `app/src/test/java/com/example/musicplayerlab/repository/MusicRepositoryTest.kt`

Cobertura observada:

- mapeo correcto de `results` a `Song`
- fallback de `audiodownload`
- reporte de `PARSE_FAILURE` cuando el JSON es inválido

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Componente del dispositivo: [`COMPONENTE_DISPOSITIVO.md`](COMPONENTE_DISPOSITIVO.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- UI Compose: [`UI_COMPOSE.md`](UI_COMPOSE.md)
- Ejecución y pruebas: [`EJECUCION_Y_PRUEBAS.md`](EJECUCION_Y_PRUEBAS.md)
- Avance fase datos: [`AVANCE_FASE_DATOS.md`](AVANCE_FASE_DATOS.md)
- Avance fase UI: [`AVANCE_FASE_UI.md`](AVANCE_FASE_UI.md)

