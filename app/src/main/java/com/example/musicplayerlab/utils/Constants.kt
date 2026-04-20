package com.example.musicplayerlab.utils

import com.example.musicplayerlab.BuildConfig

object Constants {
    const val BASE_URL = "https://api.jamendo.com/v3.0"
    const val TRACKS_ENDPOINT = "tracks"
    const val RESPONSE_FORMAT = "json"
    const val DEFAULT_LIMIT = 20
    const val CONNECTION_TIMEOUT_MS = 15_000
    const val READ_TIMEOUT_MS = 15_000
    val CLIENT_ID: String = BuildConfig.JAMENDO_CLIENT_ID
    const val SEARCH_QUERY_PARAMETER = "namesearch"

    fun isClientIdConfigured(clientId: String = CLIENT_ID): Boolean {
        return clientId.isNotBlank()
    }
}

