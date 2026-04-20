package com.example.musicplayerlab.network

import com.example.musicplayerlab.model.JamendoTrackResponse
import com.example.musicplayerlab.model.Song
import org.json.JSONArray
import org.json.JSONObject

object JsonParser {
    fun parseTrackResponse(json: String): JamendoTrackResponse {
        val root = JSONObject(json)
        val resultsArray = root.optJSONArray("results") ?: JSONArray()
        val songs = mutableListOf<Song>()

        for (index in 0 until resultsArray.length()) {
            val item = resultsArray.optJSONObject(index) ?: continue
            songs.add(item.toSong())
        }

        return JamendoTrackResponse(results = songs)
    }

    private fun JSONObject.toSong(): Song {
        return Song(
            id = readString("id"),
            title = optString("name"),
            artistName = optString("artist_name"),
            audioUrl = optString("audio").ifBlank { optString("audiodownload") },
            imageUrl = optString("image").takeIf { it.isNotBlank() },
            durationSeconds = if (has("duration") && !isNull("duration")) {
                optInt("duration")
            } else {
                null
            },
            albumName = optString("album_name").takeIf { it.isNotBlank() }
        )
    }

    private fun JSONObject.readString(key: String): String {
        val value = opt(key)
        return when (value) {
            is String -> value
            is Number -> value.toString()
            else -> ""
        }
    }
}

