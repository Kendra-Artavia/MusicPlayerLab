package com.example.musicplayerlab.repository

import com.example.musicplayerlab.network.JamendoApiClient
import com.example.musicplayerlab.utils.ErrorType
import com.example.musicplayerlab.utils.NetworkConnectivityChecker
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MusicRepositoryTest {
    @Test
    fun searchSongs_returnsErrorWhenThereIsNoInternetConnection() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = false)
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.NO_INTERNET, (result as NetworkResult.Error).type)
    }

    @Test
    fun searchSongs_propagatesClientIdErrorFromApiClient() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(clientId = "")
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.MISSING_CLIENT_ID, (result as NetworkResult.Error).type)
    }

    @Test
    fun searchSongs_returnsParsedDomainSongsWhenApiIsSuccessful() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(
                clientId = "client-id-de-prueba",
                connectionFactory = {
                    FakeHttpURLConnection(
                        responseCodeValue = HttpURLConnection.HTTP_OK,
                        responseBody = """
                            {
                              "results": [
                                {
                                  "id": 101,
                                  "name": "Tema de prueba",
                                  "artist_name": "Artista test",
                                  "audio": "https://example.com/audio.mp3"
                                }
                              ]
                            }
                        """.trimIndent()
                    )
                }
            )
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Success)
        val songs = (result as NetworkResult.Success).data
        assertEquals(1, songs.size)
        assertEquals("101", songs.first().id)
        assertEquals("Tema de prueba", songs.first().title)
    }

    @Test
    fun searchSongs_returnsErrorWhenJsonCannotBeParsed() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(
                clientId = "client-id-de-prueba",
                connectionFactory = {
                    FakeHttpURLConnection(
                        responseCodeValue = HttpURLConnection.HTTP_OK,
                        responseBody = "{ json-invalido"
                    )
                }
            )
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.PARSE_FAILURE, (result as NetworkResult.Error).type)
        assertNotNull(result.throwable)
    }

    @Test
    fun searchSongs_propagatesApiHttpErrorCode() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(
                clientId = "client-id-de-prueba",
                connectionFactory = {
                    FakeHttpURLConnection(
                        responseCodeValue = HttpURLConnection.HTTP_INTERNAL_ERROR,
                        errorBody = "Error temporal"
                    )
                }
            )
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Error)
        result as NetworkResult.Error
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, result.code)
        assertEquals(ErrorType.HTTP_SERVER, result.type)
    }

    @Test
    fun searchSongs_returnsSuccessWithEmptyListWhenApiResultsAreEmpty() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(
                clientId = "client-id-de-prueba",
                connectionFactory = {
                    FakeHttpURLConnection(
                        responseCodeValue = HttpURLConnection.HTTP_OK,
                        responseBody = """
                            {
                              "results": []
                            }
                        """.trimIndent()
                    )
                }
            )
        )

        val result = repository.searchSongs(query = "rock")

        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data.isEmpty())
    }

    @Test
    fun searchSongs_usesDefaultQueryWhenSearchTextIsBlank() = runBlocking {
        val repository = MusicRepository(
            connectivityChecker = FakeConnectivityChecker(isConnected = true),
            apiClient = JamendoApiClient(
                clientId = "client-id-de-prueba",
                connectionFactory = {
                    FakeHttpURLConnection(
                        responseCodeValue = HttpURLConnection.HTTP_OK,
                        responseBody = """
                            {
                              "results": [
                                {
                                  "id": 101,
                                  "name": "Tema de prueba",
                                  "artist_name": "Artista test",
                                  "audio": "https://example.com/audio.mp3"
                                }
                              ]
                            }
                        """.trimIndent()
                    )
                }
            )
        )

        val result = repository.searchSongs(query = "   ")

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.size)
    }

    private class FakeConnectivityChecker(
        private val isConnected: Boolean
    ) : NetworkConnectivityChecker {
        override fun isInternetAvailable(): Boolean = isConnected
    }

    private class FakeHttpURLConnection(
        private val responseCodeValue: Int,
        private val responseBody: String = "",
        private val errorBody: String? = null
    ) : HttpURLConnection(URL("https://example.com")) {
        override fun disconnect() = Unit

        override fun usingProxy(): Boolean = false

        override fun connect() = Unit

        override fun getResponseCode(): Int = responseCodeValue

        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(responseBody.toByteArray(Charsets.UTF_8))
        }

        override fun getErrorStream(): InputStream? {
            val body = errorBody ?: return null
            return ByteArrayInputStream(body.toByteArray(Charsets.UTF_8))
        }
    }
}

