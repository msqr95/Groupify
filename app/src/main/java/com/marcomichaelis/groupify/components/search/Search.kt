package com.marcomichaelis.groupify.components.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.marcomichaelis.groupify.components.VerticalList
import com.marcomichaelis.groupify.spotify.models.Track

@Composable
fun Search(tracks: List<Track>, onAdd: (Track) -> Unit, modifier: Modifier = Modifier) {
    VerticalList(modifier = modifier) {
        tracks.forEach { SearchTrack(track = it, onAdd = { onAdd(it) }) }
    }
}
