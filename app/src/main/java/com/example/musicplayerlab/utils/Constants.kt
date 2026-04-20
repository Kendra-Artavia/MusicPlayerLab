package com.example.musicplayerlab.utils

object Constants {
    const val BASE_URL = "https://api.jamendo.com/v3.0"
    const val TRACKS_ENDPOINT = "tracks"
    const val RESPONSE_FORMAT = "json"
    const val DEFAULT_LIMIT = 20
    const val CONNECTION_TIMEOUT_MS = 15_000
    const val READ_TIMEOUT_MS = 15_000
    const val CLIENT_ID_PLACEHOLDER = "YOUR_JAMENDO_CLIENT_ID"
    const val CLIENT_ID = CLIENT_ID_PLACEHOLDER
    const val SEARCH_QUERY_PARAMETER = "namesearch"

    fun isClientIdConfigured(clientId: String = CLIENT_ID): Boolean {
        return clientId.isNotBlank() && clientId != CLIENT_ID_PLACEHOLDER
    }
}

