package com.example.musicplayerlab.utils

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(
        val type: ErrorType,
        val throwable: Throwable? = null,
        val code: Int? = null,
        val debugMessage: String? = null
    ) : NetworkResult<Nothing>()
}

