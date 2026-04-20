package com.example.musicplayerlab.viewmodel

import com.example.musicplayerlab.model.Song

sealed class MusicUiState {
    object Idle : MusicUiState()

    object Loading : MusicUiState()

    data class Success(val songs: List<Song>) : MusicUiState()

    data class Error(val message: String) : MusicUiState()
}

