package com.example.musicplayerlab.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayerlab.R
import com.example.musicplayerlab.model.Song
import com.example.musicplayerlab.ui.components.HomeHeader
import com.example.musicplayerlab.ui.components.CenteredMessageState
import com.example.musicplayerlab.ui.components.EmptyState
import com.example.musicplayerlab.ui.components.ErrorState
import com.example.musicplayerlab.ui.components.HomeSearchBar
import com.example.musicplayerlab.ui.components.LoadingState
import com.example.musicplayerlab.ui.components.SongListSuccessState
import com.example.musicplayerlab.viewmodel.MusicUiState
import com.example.musicplayerlab.viewmodel.SongListFilter
import com.example.musicplayerlab.viewmodel.MusicViewModel

@Composable
fun MusicPlayerLabApp(
    viewModel: MusicViewModel = viewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.loadInitialSongs()
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle().value
    val songListFilter = viewModel.songListFilter.collectAsStateWithLifecycle().value
    val filteredSongs = viewModel.filteredSongs.collectAsStateWithLifecycle().value
    val previewPlaybackState = viewModel.previewPlaybackState.collectAsStateWithLifecycle().value
    val favoriteSongIds = viewModel.favoriteSongIds.collectAsStateWithLifecycle().value
    val contentSource = viewModel.contentSource.collectAsStateWithLifecycle().value

    HomeScreen(
        uiState = uiState,
        onRetry = viewModel::loadInitialSongs,
        searchQuery = searchQuery,
        songListFilter = songListFilter,
        contentSource = contentSource,
        filteredSongs = filteredSongs,
        favoriteSongIds = favoriteSongIds,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearch = viewModel::onSearch,
        onClearQuery = { viewModel.onSearch("") },
        onSongListFilterChange = viewModel::onSongListFilterChange,
        previewPlaybackState = previewPlaybackState,
        onPreviewPlayPause = viewModel::onPreviewPlayPause,
        onPreviewStop = viewModel::onPreviewStop,
        onFavoriteToggle = viewModel::toggleFavorite
    )
}

@Composable
fun HomeScreen(
    uiState: MusicUiState,
    onRetry: () -> Unit,
    searchQuery: String,
    songListFilter: SongListFilter,
    contentSource: MusicViewModel.ContentSource,
    filteredSongs: List<Song>,
    favoriteSongIds: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSongListFilterChange: (SongListFilter) -> Unit,
    previewPlaybackState: com.example.musicplayerlab.viewmodel.PreviewPlaybackState,
    onPreviewPlayPause: (Song) -> Unit,
    onPreviewStop: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeader()

                HomeSearchBar(
                    query = searchQuery,
                    selectedFilter = songListFilter,
                    onQueryChange = onSearchQueryChange,
                    onSearch = onSearch,
                    onClearQuery = onClearQuery,
                    onFilterChange = onSongListFilterChange
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HomeStateContent(
                        uiState = uiState,
                        filteredSongs = filteredSongs,
                        favoriteSongIds = favoriteSongIds,
                        songListFilter = songListFilter,
                        contentSource = contentSource,
                        searchQuery = searchQuery,
                        previewPlaybackState = previewPlaybackState,
                        onPreviewPlayPause = onPreviewPlayPause,
                        onPreviewStop = onPreviewStop,
                        onFavoriteToggle = onFavoriteToggle,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeStateContent(
    uiState: MusicUiState,
    filteredSongs: List<Song>,
    favoriteSongIds: Set<String>,
    songListFilter: SongListFilter,
    contentSource: MusicViewModel.ContentSource,
    searchQuery: String,
    previewPlaybackState: com.example.musicplayerlab.viewmodel.PreviewPlaybackState,
    onPreviewPlayPause: (Song) -> Unit,
    onPreviewStop: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onRetry: () -> Unit
) {
    val hasActiveSearch = contentSource == MusicViewModel.ContentSource.SEARCH && searchQuery.isNotBlank()

    when (uiState) {
        MusicUiState.Idle -> CenteredMessageState(
            messageResId = R.string.home_idle_message
        )

        MusicUiState.Loading -> LoadingState(
            messageResId = if (hasActiveSearch) {
                R.string.home_search_loading_message
            } else {
                R.string.home_loading_message
            }
        )

        is MusicUiState.Success -> {
            if (filteredSongs.isEmpty()) {
                val emptyMessageResId = when {
                    songListFilter == SongListFilter.FAVORITES_ONLY && searchQuery.isBlank() -> {
                        R.string.home_favorites_empty_message
                    }
                    songListFilter == SongListFilter.FAVORITES_ONLY && hasActiveSearch -> {
                        R.string.home_favorites_search_empty_message
                    }
                    searchQuery.isBlank() -> R.string.home_empty_message
                    hasActiveSearch -> R.string.home_search_empty_message
                    else -> R.string.home_empty_message
                }
                val supportingMessageResId = when {
                    songListFilter == SongListFilter.FAVORITES_ONLY && searchQuery.isBlank() -> {
                        R.string.home_favorites_empty_hint
                    }

                    songListFilter == SongListFilter.FAVORITES_ONLY && hasActiveSearch -> {
                        R.string.home_favorites_search_empty_hint
                    }

                    searchQuery.isBlank() -> R.string.home_empty_hint

                    hasActiveSearch -> R.string.home_search_empty_hint

                    else -> R.string.home_empty_hint
                }
                EmptyState(
                    messageResId = emptyMessageResId,
                    supportingMessageResId = supportingMessageResId,
                    onReload = onRetry
                )
            } else {
                SongListSuccessState(
                    songs = filteredSongs,
                    previewPlaybackState = previewPlaybackState,
                    onPreviewPlayPause = onPreviewPlayPause,
                    onPreviewStop = onPreviewStop,
                    favoriteSongIds = favoriteSongIds,
                    onFavoriteToggle = onFavoriteToggle,
                    onReload = onRetry
                )
            }
        }

        is MusicUiState.Error -> ErrorState(
            messageResId = uiState.messageResId,
            supportingMessageResId = when (uiState.messageResId) {
                R.string.error_network_io,
                R.string.error_timeout,
                R.string.error_no_internet -> R.string.error_connection_hint

                else -> if (hasActiveSearch) {
                    R.string.home_search_error_hint
                } else {
                    null
                }
            },
            actionResId = if (hasActiveSearch) {
                R.string.home_search_retry_action
            } else {
                R.string.home_retry_action
            },
            onRetry = onRetry
        )
    }
}



