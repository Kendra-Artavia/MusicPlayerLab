package com.example.musicplayerlab.repository

import com.example.musicplayerlab.model.JamendoTrackResponse
import com.example.musicplayerlab.network.JamendoApiClient
import com.example.musicplayerlab.network.JsonParser
import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.NetworkResult

class MusicRepository @JvmOverloads constructor(
    private val apiClient: JamendoApiClient = JamendoApiClient(),
    private val jsonParser: JsonParser = JsonParser
) {
    suspend fun searchSongs(
        query: String,
        limit: Int = Constants.DEFAULT_LIMIT
    ): NetworkResult<JamendoTrackResponse> {
        return when (val result = apiClient.searchTracks(query, limit)) {
            is NetworkResult.Success -> {
                try {
                    NetworkResult.Success(jsonParser.parseTrackResponse(result.data))
                } catch (exception: Exception) {
                    NetworkResult.Error(
                        message = "Failed to parse Jamendo response.",
                        throwable = exception
                    )
                }
            }

            is NetworkResult.Error -> result
        }
    }
}

