package com.example.musicplayerlab.network

import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.ErrorType
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder

class JamendoApiClient @JvmOverloads constructor(
    private val clientId: String = Constants.CLIENT_ID,
    private val connectionFactory: (String) -> HttpURLConnection = { url ->
        URL(url).openConnection() as HttpURLConnection
    }
) {
    suspend fun searchSongsByText(
        searchText: String,
        limit: Int = Constants.DEFAULT_LIMIT
    ): NetworkResult<String> {
        return searchTracks(query = searchText, limit = limit)
    }

    suspend fun searchTracks(
        query: String,
        limit: Int = Constants.DEFAULT_LIMIT
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            return@withContext NetworkResult.Error(type = ErrorType.EMPTY_QUERY)
        }

        if (!Constants.isClientIdConfigured(clientId)) {
            return@withContext NetworkResult.Error(type = ErrorType.MISSING_CLIENT_ID)
        }

        var connection: HttpURLConnection? = null

        try {
            connection = createConnection(buildSearchUrl(trimmedQuery, limit.coerceAtLeast(1)))
            val responseCode = connection.responseCode
            val responseBody = readResponseBody(connection, responseCode)

            when {
                responseCode == HttpURLConnection.HTTP_OK -> {
                    if (responseBody.isNotBlank()) {
                        NetworkResult.Success(responseBody)
                    } else {
                        NetworkResult.Error(
                            type = ErrorType.EMPTY_RESPONSE_BODY,
                            code = responseCode
                        )
                    }
                }

                responseCode in 400..499 -> {
                    NetworkResult.Error(
                        type = ErrorType.HTTP_CLIENT,
                        debugMessage = buildHttpDebugMessage(
                            prefix = "Jamendo request error",
                            responseCode = responseCode,
                            responseBody = responseBody
                        ),
                        code = responseCode
                    )
                }

                responseCode in 500..599 -> {
                    NetworkResult.Error(
                        type = ErrorType.HTTP_SERVER,
                        debugMessage = buildHttpDebugMessage(
                            prefix = "Jamendo server error",
                            responseCode = responseCode,
                            responseBody = responseBody
                        ),
                        code = responseCode
                    )
                }

                else -> {
                    NetworkResult.Error(
                        type = ErrorType.HTTP_UNEXPECTED,
                        debugMessage = buildHttpDebugMessage(
                            prefix = "Unexpected Jamendo HTTP response",
                            responseCode = responseCode,
                            responseBody = responseBody
                        ),
                        code = responseCode
                    )
                }
            }
        } catch (_: SocketTimeoutException) {
            NetworkResult.Error(type = ErrorType.TIMEOUT)
        } catch (exception: IOException) {
            NetworkResult.Error(
                type = ErrorType.NETWORK_IO,
                throwable = exception
            )
        } catch (exception: Exception) {
            NetworkResult.Error(
                type = ErrorType.UNKNOWN,
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
        return connectionFactory(urlString).apply {
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
        val stream = runCatching {
            if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
        }.getOrNull()

        return stream
            ?.bufferedReader()
            ?.use { it.readText() }
            ?.trim()
            .orEmpty()
    }

    private fun buildHttpDebugMessage(
        prefix: String,
        responseCode: Int,
        responseBody: String
    ): String {
        val bodyPreview = responseBody
            .lineSequence()
            .firstOrNull { it.isNotBlank() }
            .orEmpty()
            .take(200)

        return if (bodyPreview.isNotBlank()) {
            "$prefix (HTTP $responseCode): $bodyPreview"
        } else {
            "$prefix (HTTP $responseCode)."
        }
    }
}

