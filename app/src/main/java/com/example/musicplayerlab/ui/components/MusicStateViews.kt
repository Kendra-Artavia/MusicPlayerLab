package com.example.musicplayerlab.ui.components

import androidx.annotation.StringRes
import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayerlab.R
import com.example.musicplayerlab.model.Song
import com.example.musicplayerlab.viewmodel.PreviewPlaybackState

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    @StringRes messageResId: Int = R.string.home_loading_message
) {
    CenteredStateContainer(modifier = modifier) {
        CircularProgressIndicator()
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    @StringRes messageResId: Int = R.string.home_empty_message,
    @StringRes supportingMessageResId: Int? = null,
    onReload: (() -> Unit)? = null,
    @StringRes actionResId: Int = R.string.home_reload_action
) {
    CenteredStateContainer(modifier = modifier) {
        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        supportingMessageResId?.let {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (onReload != null) {
            ReloadAction(
                modifier = Modifier.padding(top = 16.dp),
                onReload = onReload,
                actionResId = actionResId
            )
        }
    }
}

@Composable
fun ErrorState(
    @StringRes messageResId: Int,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    @StringRes supportingMessageResId: Int? = null,
    @StringRes actionResId: Int = R.string.home_retry_action
) {
    CenteredStateContainer(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        supportingMessageResId?.let {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(it),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetry
        ) {
            Text(text = stringResource(actionResId))
        }
    }
}

@Composable
fun CenteredMessageState(
    @StringRes messageResId: Int,
    modifier: Modifier = Modifier
) {
    CenteredStateContainer(modifier = modifier) {
        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SongListSuccessState(
    songs: List<Song>,
    modifier: Modifier = Modifier,
    @PluralsRes countMessagePluralResId: Int = R.plurals.home_success_count,
    previewPlaybackState: PreviewPlaybackState,
    favoriteSongIds: Set<String>,
    onPreviewPlayPause: (Song) -> Unit,
    onPreviewStop: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onReload: (() -> Unit)? = null,
    @StringRes actionResId: Int = R.string.home_reload_action,
    @StringRes unknownTitleResId: Int = R.string.song_unknown_title,
    @StringRes unknownArtistResId: Int = R.string.song_unknown_artist
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = pluralStringResource(countMessagePluralResId, songs.size, songs.size),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (onReload != null) {
                    ReloadAction(
                        onReload = onReload,
                        actionResId = actionResId
                    )
                }
            }

            if (previewPlaybackState.currentSongId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onPreviewStop) {
                        Text(text = stringResource(R.string.song_stop_action))
                    }
                }
            }
        }

        items(
            items = songs,
            key = { it.id }
        ) { song ->
            SongRow(
                song = song,
                previewPlaybackState = previewPlaybackState,
                favoriteSongIds = favoriteSongIds,
                onPreviewPlayPause = onPreviewPlayPause,
                onPreviewStop = onPreviewStop,
                onFavoriteToggle = onFavoriteToggle,
                unknownTitleResId = unknownTitleResId,
                unknownArtistResId = unknownArtistResId
            )
        }
    }
}

@Composable
private fun ReloadAction(
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes actionResId: Int = R.string.home_reload_action
) {
    Button(
        modifier = modifier,
        onClick = onReload
    ) {
        Text(text = stringResource(actionResId))
    }
}

@Composable
fun SongRow(
    song: Song,
    modifier: Modifier = Modifier,
    previewPlaybackState: PreviewPlaybackState,
    favoriteSongIds: Set<String>,
    onPreviewPlayPause: (Song) -> Unit,
    onPreviewStop: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    @StringRes unknownTitleResId: Int = R.string.song_unknown_title,
    @StringRes unknownArtistResId: Int = R.string.song_unknown_artist,
    @StringRes albumMessageResId: Int = R.string.song_album_message,
    @StringRes durationMessageResId: Int = R.string.song_duration_message,
    @StringRes durationValueResId: Int = R.string.song_duration_value
) {
    val title = song.title.trim().ifBlank { stringResource(unknownTitleResId) }
    val artist = song.artistName.trim().ifBlank { stringResource(unknownArtistResId) }
    val album = song.albumName?.trim()?.takeIf { it.isNotBlank() }
    val durationText = song.durationSeconds
        ?.takeIf { it > 0 }
        ?.let { durationSeconds ->
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            stringResource(durationValueResId, minutes, seconds)
        }
    val hasCover = !song.imageUrl.isNullOrBlank()
    val hasPreview = song.audioUrl.isNotBlank()
    val isCurrentPreview = previewPlaybackState.currentSongId == song.id
    val isPlayingPreview = isCurrentPreview && previewPlaybackState.isPlaying
    val isPreviewPreparing = isCurrentPreview && previewPlaybackState.isPreparing
    val isFavorite = favoriteSongIds.contains(song.id)
    val playPauseLabelResId = if (isCurrentPreview && previewPlaybackState.isPlaying) {
        R.string.song_pause_action
    } else {
        R.string.song_play_action
    }

    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasCover) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 2.dp
                    ) {
                        // Visual placeholder for future image loading.
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = artist,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            FilterChip(
                selected = isFavorite,
                onClick = { onFavoriteToggle(song.id) },
                label = {
                    Text(
                        text = if (isFavorite) {
                            stringResource(R.string.song_unfavorite_action)
                        } else {
                            stringResource(R.string.song_favorite_action)
                        }
                    )
                }
            )

            if (isFavorite) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = stringResource(R.string.song_favorite_indicator),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            album?.let {
                Text(
                    text = stringResource(albumMessageResId, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            durationText?.let {
                Text(
                    text = stringResource(durationMessageResId, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isPreviewPreparing) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(text = stringResource(R.string.song_preparing_indicator))
                    }
                )
            } else if (isPlayingPreview) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(text = stringResource(R.string.song_playing_indicator))
                    }
                )
            }

            if (hasPreview) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPreviewPlayPause(song) },
                        enabled = !isPreviewPreparing
                    ) {
                        Text(text = stringResource(playPauseLabelResId))
                    }

                    if (isCurrentPreview) {
                        OutlinedButton(onClick = onPreviewStop) {
                            Text(text = stringResource(R.string.song_stop_action))
                        }
                    }
                }
            } else if (hasCover) {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(text = stringResource(R.string.song_cover_available))
                    }
                )
            }
        }
    }
}


@Composable
private fun CenteredStateContainer(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}


