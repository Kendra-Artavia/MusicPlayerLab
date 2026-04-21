package com.example.musicplayerlab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerlab.R
import com.example.musicplayerlab.model.Song
import com.example.musicplayerlab.player.PreviewPlayerManager
import com.example.musicplayerlab.repository.FavoritesRepository
import com.example.musicplayerlab.repository.MusicRepository
import com.example.musicplayerlab.utils.AndroidNetworkConnectivityChecker
import com.example.musicplayerlab.utils.Constants
import com.example.musicplayerlab.utils.ErrorType
import com.example.musicplayerlab.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MusicViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: MusicRepository = MusicRepository(
        connectivityChecker = AndroidNetworkConnectivityChecker(application)
    )
) : AndroidViewModel(application) {
    private val previewPlayerManager = PreviewPlayerManager()
    private val favoritesRepository = FavoritesRepository(application)
    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _songListFilter = MutableStateFlow(SongListFilter.ALL)
    val songListFilter: StateFlow<SongListFilter> = _songListFilter.asStateFlow()

    private val _previewPlaybackState = MutableStateFlow(PreviewPlaybackState())
    val previewPlaybackState: StateFlow<PreviewPlaybackState> = _previewPlaybackState.asStateFlow()

    val favoriteSongIds: StateFlow<Set<String>> = favoritesRepository.favoriteSongIds

    val filteredSongs: StateFlow<List<Song>> = combine(
        _allSongs,
        _searchQuery,
        _songListFilter,
        favoriteSongIds
    ) { songs, query, listFilter, favorites ->
        filterSongs(
            songs = songs,
            query = query,
            listFilter = listFilter,
            favoriteSongIds = favorites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun loadInitialSongs() {
        searchSongs(Constants.DEFAULT_INITIAL_QUERY)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSongListFilterChange(filter: SongListFilter) {
        _songListFilter.value = filter
    }

    fun toggleFavorite(songId: String) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(songId)
        }
    }

    fun onPreviewPlayPause(song: Song) {
        if (song.audioUrl.isBlank()) return

        val currentState = _previewPlaybackState.value
        when {
            currentState.currentSongId == song.id && currentState.isPlaying -> pausePreview()
            currentState.currentSongId == song.id && currentState.isPreparing -> return
            currentState.currentSongId == song.id && !currentState.isPlaying -> resumePreview()
            else -> playPreview(song)
        }
    }

    fun onPreviewStop() {
        previewPlayerManager.stop()
        _previewPlaybackState.value = PreviewPlaybackState()
    }

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
                    _allSongs.value = result.data
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

    private fun filterSongs(
        songs: List<Song>,
        query: String,
        listFilter: SongListFilter,
        favoriteSongIds: Set<String>
    ): List<Song> {
        val trimmedQuery = query.trim()
        val baseSongs = when (listFilter) {
            SongListFilter.ALL -> songs
            SongListFilter.FAVORITES_ONLY -> songs.filter { song -> favoriteSongIds.contains(song.id) }
        }

        if (trimmedQuery.isBlank()) {
            return baseSongs
        }

        return baseSongs.filter { song ->
            song.title.contains(trimmedQuery, ignoreCase = true) ||
                song.artistName.contains(trimmedQuery, ignoreCase = true) ||
                (song.albumName?.contains(trimmedQuery, ignoreCase = true) == true)
        }
    }

    private fun playPreview(song: Song) {
        _previewPlaybackState.value = PreviewPlaybackState(
            currentSongId = song.id,
            isPlaying = false,
            isPreparing = true
        )

        previewPlayerManager.play(
            song = song,
            onPrepared = {
                _previewPlaybackState.value = PreviewPlaybackState(
                    currentSongId = song.id,
                    isPlaying = true,
                    isPreparing = false
                )
            },
            onCompleted = {
                _previewPlaybackState.value = PreviewPlaybackState()
            },
            onError = {
                _previewPlaybackState.value = PreviewPlaybackState()
            }
        )
    }

    private fun pausePreview() {
        if (previewPlayerManager.pause()) {
            _previewPlaybackState.value = _previewPlaybackState.value.copy(isPlaying = false)
        }
    }

    private fun resumePreview() {
        if (previewPlayerManager.resume()) {
            _previewPlaybackState.value = _previewPlaybackState.value.copy(isPlaying = true)
        }
    }

    override fun onCleared() {
        previewPlayerManager.release()
        super.onCleared()
    }

}



