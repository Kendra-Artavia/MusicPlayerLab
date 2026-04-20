package com.example.musicplayerlab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerlab.R
import com.example.musicplayerlab.repository.MusicRepository
import com.example.musicplayerlab.utils.AndroidNetworkConnectivityChecker
import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.ErrorType
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MusicViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: MusicRepository = MusicRepository(
        connectivityChecker = AndroidNetworkConnectivityChecker(application)
    )
) : AndroidViewModel(application) {
    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    fun searchSongs(query: String, limit: Int = Constants.DEFAULT_LIMIT) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            _uiState.value = MusicUiState.Error(R.string.error_empty_query)
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            when (val result = repository.searchSongs(trimmedQuery, limit)) {
                is NetworkResult.Success -> {
                    _uiState.value = MusicUiState.Success(result.data)
                }

                is NetworkResult.Error -> {
                    _uiState.value = MusicUiState.Error(mapErrorToMessageRes(result.type))
                }
            }
        }
    }

    private fun mapErrorToMessageRes(errorType: ErrorType): Int {
        return when (errorType) {
            ErrorType.EMPTY_QUERY -> R.string.error_empty_query
            ErrorType.MISSING_CLIENT_ID -> R.string.error_missing_client_id
            ErrorType.EMPTY_RESPONSE_BODY -> R.string.error_empty_response_body
            ErrorType.HTTP_CLIENT -> R.string.error_http_client
            ErrorType.HTTP_SERVER -> R.string.error_http_server
            ErrorType.HTTP_UNEXPECTED -> R.string.error_http_unexpected
            ErrorType.TIMEOUT -> R.string.error_timeout
            ErrorType.NETWORK_IO -> R.string.error_network_io
            ErrorType.NO_INTERNET -> R.string.error_no_internet
            ErrorType.PARSE_FAILURE -> R.string.error_parse_failure
            ErrorType.UNKNOWN -> R.string.error_unknown
        }
    }

    fun clearState() {
        searchJob?.cancel()
        _uiState.value = MusicUiState.Idle
    }
}



