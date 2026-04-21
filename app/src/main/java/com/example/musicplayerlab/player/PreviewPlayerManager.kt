package com.example.musicplayerlab.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.musicplayerlab.model.Song

class PreviewPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun play(
        song: Song,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit,
        onError: () -> Unit
    ) {
        stop()

        val player = MediaPlayer()
        mediaPlayer = player

        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(song.audioUrl)
            player.setOnPreparedListener {
                if (mediaPlayer !== it) {
                    return@setOnPreparedListener
                }
                it.start()
                onPrepared()
            }
            player.setOnCompletionListener {
                if (mediaPlayer !== it) {
                    return@setOnCompletionListener
                }
                releasePlayer()
                onCompleted()
            }
            player.setOnErrorListener { _, _, _ ->
                releasePlayer()
                onError()
                true
            }
            player.prepareAsync()
        } catch (exception: Exception) {
            releasePlayer()
            onError()
        }
    }

    fun pause(): Boolean {
        val player = mediaPlayer ?: return false
        if (!player.isPlaying) return false
        player.pause()
        return true
    }

    fun resume(): Boolean {
        val player = mediaPlayer ?: return false
        if (player.isPlaying) return false
        player.start()
        return true
    }

    fun stop(): Boolean {
        val hasPlayer = mediaPlayer != null
        releasePlayer()
        return hasPlayer
    }

    fun release() {
        releasePlayer()
    }

    private fun releasePlayer() {
        mediaPlayer?.runCatching {
            reset()
            release()
        }
        mediaPlayer = null
    }
}


