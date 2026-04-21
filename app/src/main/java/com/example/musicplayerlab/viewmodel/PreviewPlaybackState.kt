package com.example.musicplayerlab.viewmodel

data class PreviewPlaybackState(
    val currentSongId: String? = null,
    val isPlaying: Boolean = false,
    val isPreparing: Boolean = false
)


