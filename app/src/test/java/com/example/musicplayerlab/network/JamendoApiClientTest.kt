package com.example.musicplayerlab.network

import com.example.musicplayerlab.utils.NetworkResult
import com.example.musicplayerlab.utils.ErrorType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class JamendoApiClientTest {
    @Test
    fun searchTracks_returnsErrorWhenQueryIsBlank() = runBlocking {
        val apiClient = JamendoApiClient(clientId = "client-id-de-prueba")

        val result = apiClient.searchTracks(query = "   ")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.EMPTY_QUERY, (result as NetworkResult.Error).type)
    }

    @Test
    fun searchTracks_returnsErrorWhenClientIdIsNotConfigured() = runBlocking {
        val apiClient = JamendoApiClient(clientId = "")

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.MISSING_CLIENT_ID, (result as NetworkResult.Error).type)
    }

    @Test
    fun searchTracks_returnsErrorWhenHttp200HasEmptyBody() = runBlocking {
        val apiClient = JamendoApiClient(
            clientId = "client-id-de-prueba",
            connectionFactory = {
                FakeHttpURLConnection(responseCodeValue = HttpURLConnection.HTTP_OK, responseBody = "")
            }
        )

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.EMPTY_RESPONSE_BODY, (result as NetworkResult.Error).type)
        assertEquals(HttpURLConnection.HTTP_OK, result.code)
    }

    @Test
    fun searchTracks_returnsErrorFor4xxResponse() = runBlocking {
        val apiClient = JamendoApiClient(
            clientId = "client-id-de-prueba",
            connectionFactory = {
                FakeHttpURLConnection(
                    responseCodeValue = HttpURLConnection.HTTP_BAD_REQUEST,
                    errorBody = "Parametro de busqueda invalido"
                )
            }
        )

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        result as NetworkResult.Error
        assertEquals(ErrorType.HTTP_CLIENT, result.type)
        assertTrue(result.debugMessage?.contains("Jamendo request error") == true)
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, result.code)
    }

    @Test
    fun searchTracks_returnsErrorFor5xxResponse() = runBlocking {
        val apiClient = JamendoApiClient(
            clientId = "client-id-de-prueba",
            connectionFactory = {
                FakeHttpURLConnection(
                    responseCodeValue = HttpURLConnection.HTTP_INTERNAL_ERROR,
                    errorBody = "Fallo interno"
                )
            }
        )

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        result as NetworkResult.Error
        assertEquals(ErrorType.HTTP_SERVER, result.type)
        assertTrue(result.debugMessage?.contains("Jamendo server error") == true)
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, result.code)
    }

    @Test
    fun searchTracks_returnsTimeoutErrorWhenConnectionTimesOut() = runBlocking {
        val apiClient = JamendoApiClient(
            clientId = "client-id-de-prueba",
            connectionFactory = {
                throw SocketTimeoutException("timeout")
            }
        )

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.TIMEOUT, (result as NetworkResult.Error).type)
        assertNull(result.throwable)
    }

    @Test
    fun searchTracks_returnsNetworkErrorWhenIOExceptionOccurs() = runBlocking {
        val apiClient = JamendoApiClient(
            clientId = "client-id-de-prueba",
            connectionFactory = {
                throw IOException("sin red")
            }
        )

        val result = apiClient.searchTracks(query = "rock")

        assertTrue(result is NetworkResult.Error)
        assertEquals(ErrorType.NETWORK_IO, (result as NetworkResult.Error).type)
        assertTrue(result.throwable is IOException)
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


