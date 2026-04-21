package com.example.musicplayerlab.repository

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FavoritesRepository(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        FAVORITES_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _favoriteSongIds = MutableStateFlow(loadFavoriteSongIds())
    val favoriteSongIds: StateFlow<Set<String>> = _favoriteSongIds.asStateFlow()

    suspend fun toggleFavorite(songId: String) {
        withContext(Dispatchers.IO) {
            val currentFavorites = _favoriteSongIds.value
            val updatedFavorites = if (currentFavorites.contains(songId)) {
                currentFavorites - songId
            } else {
                currentFavorites + songId
            }

            sharedPreferences.edit {
                putStringSet(FAVORITE_SONG_IDS_KEY, updatedFavorites)
            }
            _favoriteSongIds.value = updatedFavorites
        }
    }

    private fun loadFavoriteSongIds(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITE_SONG_IDS_KEY, emptySet())
            ?.toSet()
            ?: emptySet()
    }

    private companion object {
        const val FAVORITES_PREFS_NAME = "favorite_songs"
        const val FAVORITE_SONG_IDS_KEY = "favorite_song_ids"
    }
}


