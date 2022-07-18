package com.marcomichaelis.groupify.components.playlist

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.marcomichaelis.groupify.components.Track
import com.marcomichaelis.groupify.spotify.models.Track as TrackModel

@Composable
fun PlaylistTrack(
    track: TrackModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    showDeleteButton: Boolean
) {
    Track(modifier = Modifier.testTag("playlistTrack"), track = track, onClick = onClick) {
        if (showDeleteButton) {
            IconButton(onClick = onDelete, modifier = Modifier.testTag("playlistTrackDelete")) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
