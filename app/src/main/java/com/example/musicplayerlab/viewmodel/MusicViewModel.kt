package com.example.musicplayerlab.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicplayerlab.repository.MusicRepository
import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel @JvmOverloads constructor(
    private val repository: MusicRepository = MusicRepository()
) : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    fun searchSongs(query: String, limit: Int = Constants.DEFAULT_LIMIT) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            _uiState.value = MusicUiState.Error("Search query cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            when (val result = repository.searchSongs(trimmedQuery, limit)) {
                is NetworkResult.Success -> {
                    _uiState.value = MusicUiState.Success(result.data.results)
                }

                is NetworkResult.Error -> {
                    _uiState.value = MusicUiState.Error(result.message)
                }
            }
        }
    }

    fun clearState() {
        _uiState.value = MusicUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}



