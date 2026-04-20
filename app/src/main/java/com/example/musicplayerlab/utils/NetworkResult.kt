package com.example.musicplayerlab.utils

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val code: Int? = null
    ) : NetworkResult<Nothing>()
}

