package com.example.musicplayerlab.network

import org.junit.Assert.assertEquals
import org.junit.Test

class JsonParserTest {
    @Test
    fun parseTrackResponse_mapsResultsToSongs() {
        val json = """
            {
              "results": [
                {
                  "id": 123,
                  "name": "Canción de prueba",
                  "artist_name": "Artista de prueba",
                  "audio": "https://example.com/audio.mp3",
                  "image": "https://example.com/image.jpg",
                  "duration": 240,
                  "album_name": "Álbum de prueba"
                }
              ]
            }
        """.trimIndent()

        val response = JsonParser.parseTrackResponse(json)

        assertEquals(1, response.results.size)
        val song = response.results.first()
        assertEquals("123", song.id)
        assertEquals("Canción de prueba", song.title)
        assertEquals("Artista de prueba", song.artistName)
        assertEquals("https://example.com/audio.mp3", song.audioUrl)
        assertEquals("https://example.com/image.jpg", song.imageUrl)
        assertEquals(240, song.durationSeconds)
        assertEquals("Álbum de prueba", song.albumName)
    }

    @Test
    fun parseTrackResponse_usesDownloadAudioAsFallback() {
        val json = """
            {
              "results": [
                {
                  "id": "track-1",
                  "name": "Otra canción",
                  "artist_name": "Otro artista",
                  "audiodownload": "https://example.com/download.mp3"
                }
              ]
            }
        """.trimIndent()

        val response = JsonParser.parseTrackResponse(json)

        assertEquals(1, response.results.size)
        assertEquals("https://example.com/download.mp3", response.results.first().audioUrl)
    }
}

