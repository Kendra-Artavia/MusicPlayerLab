package com.example.musicplayerlab.network

import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class JamendoApiClient @JvmOverloads constructor(
    private val clientId: String = Constants.CLIENT_ID
) {
    suspend fun searchTracks(
        query: String,
        limit: Int = Constants.DEFAULT_LIMIT
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            return@withContext NetworkResult.Error("Search query cannot be empty.")
        }

        if (!Constants.isClientIdConfigured(clientId)) {
            return@withContext NetworkResult.Error("Jamendo client_id is not configured.")
        }

        var connection: HttpURLConnection? = null

        try {
            connection = createConnection(buildSearchUrl(trimmedQuery, limit.coerceAtLeast(1)))
            val responseCode = connection.responseCode
            val responseBody = readResponseBody(connection, responseCode)

            if (responseCode in 200..299) {
                if (responseBody.isNotBlank()) {
                    NetworkResult.Success(responseBody)
                } else {
                    NetworkResult.Error(
                        message = "The server returned an empty response.",
                        code = responseCode
                    )
                }
            } else {
                NetworkResult.Error(
                    message = responseBody.ifBlank { "HTTP request failed." },
                    code = responseCode
                )
            }
        } catch (exception: Exception) {
            NetworkResult.Error(
                message = "Network request failed.",
                throwable = exception
            )
        } finally {
            connection?.disconnect()
        }
    }

    private fun buildSearchUrl(query: String, limit: Int): String {
        val encodedClientId = URLEncoder.encode(clientId, Charsets.UTF_8.name())
        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())

        return buildString {
            append(Constants.BASE_URL)
            append('/')
            append(Constants.TRACKS_ENDPOINT)
            append("/?client_id=")
            append(encodedClientId)
            append("&format=")
            append(Constants.RESPONSE_FORMAT)
            append("&limit=")
            append(limit)
            append('&')
            append(Constants.SEARCH_QUERY_PARAMETER)
            append('=')
            append(encodedQuery)
        }
    }

    private fun createConnection(urlString: String): HttpURLConnection {
        return (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = Constants.CONNECTION_TIMEOUT_MS
            readTimeout = Constants.READ_TIMEOUT_MS
            doInput = true
            setRequestProperty("Accept", "application/json")
        }
    }

    private fun readResponseBody(
        connection: HttpURLConnection,
        responseCode: Int
    ): String {
        val stream = if (responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        return stream?.bufferedReader()?.use { it.readText() }.orEmpty()
    }
}

