package com.example.musicplayerlab.repository

import com.example.musicplayerlab.model.Song
import com.example.musicplayerlab.network.JamendoApiClient
import com.example.musicplayerlab.network.JsonParser
import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.ErrorType
import com.example.musicplayerlab.utils.NetworkConnectivityChecker
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository constructor(
    private val connectivityChecker: NetworkConnectivityChecker,
    private val apiClient: JamendoApiClient = JamendoApiClient(),
    private val jsonParser: JsonParser = JsonParser
) {
    suspend fun searchSongs(
        query: String,
        limit: Int = Constants.DEFAULT_LIMIT
    ): NetworkResult<List<Song>> = withContext(Dispatchers.IO) {
        if (!connectivityChecker.isInternetAvailable()) {
            return@withContext NetworkResult.Error(
                type = ErrorType.NO_INTERNET
            )
        }

        when (val result = apiClient.searchTracks(query, limit)) {
            is NetworkResult.Success -> {
                try {
                    val songs = jsonParser.parseTrackResponse(result.data).results
                    NetworkResult.Success(songs)
                } catch (exception: Exception) {
                    NetworkResult.Error(
                        type = ErrorType.PARSE_FAILURE,
                        throwable = exception
                    )
                }
            }

            is NetworkResult.Error -> mapApiError(result)
        }
    }

    private fun mapApiError(error: NetworkResult.Error): NetworkResult.Error {
        val normalizedDebugMessage = error.debugMessage
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "Unable to fetch songs from Jamendo."

        return NetworkResult.Error(
            type = error.type,
            throwable = error.throwable,
            code = error.code,
            debugMessage = normalizedDebugMessage
        )
    }
}

