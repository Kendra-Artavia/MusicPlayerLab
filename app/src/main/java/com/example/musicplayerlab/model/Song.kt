package com.example.musicplayerlab.model

data class Song(
    val id: String,
    val title: String,
    val artistName: String,
    val audioUrl: String,
    val imageUrl: String?,
    val durationSeconds: Int?,
    val albumName: String?
)

