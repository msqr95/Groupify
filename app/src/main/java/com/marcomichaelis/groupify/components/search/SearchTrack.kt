package com.marcomichaelis.groupify.components.search

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.marcomichaelis.groupify.components.Track
import com.marcomichaelis.groupify.spotify.models.Track

@Composable
fun SearchTrack(
    track: Track,
    onAdd: () -> Unit,
) {
    Track(modifier = Modifier.testTag("searchTrack"), track = track, onClick = {}) {
        IconButton(
            modifier = Modifier.testTag("searchTrackAdd"),
            onClick = onAdd,
            enabled = !track.alreadyInPlaylist
        ) {
            if (track.alreadyInPlaylist) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Already in playlist")
            } else {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
