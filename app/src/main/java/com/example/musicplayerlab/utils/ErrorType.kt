package com.example.musicplayerlab.utils

enum class ErrorType {
    EMPTY_QUERY,
    MISSING_CLIENT_ID,
    EMPTY_RESPONSE_BODY,
    HTTP_CLIENT,
    HTTP_SERVER,
    HTTP_UNEXPECTED,
    TIMEOUT,
    NETWORK_IO,
    NO_INTERNET,
    PARSE_FAILURE,
    UNKNOWN
}

