package com.marcomichaelis.groupify.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.components.search.SearchBar
import com.marcomichaelis.groupify.components.Header
import com.marcomichaelis.groupify.components.rememberPlaylist
import com.marcomichaelis.groupify.components.search.Search
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.models.Track
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchPage() {
    val spotifyClient = LocalSpotifyClientContext.current.client
    val searchValue = remember {
        MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val searchResult = remember { mutableStateOf(listOf<Track>()) }
    val playlist = rememberPlaylist()

    LaunchedEffect(true) {
        searchValue.debounce(500).collect {
            searchResult.value =
                spotifyClient.search(it).onEach {
                    it.alreadyInPlaylist =
                        playlist.tracks.value.any { track -> track.uri == it.uri }
                }
        }
    }

    val results =
        searchResult.value.map {
            it.apply {
                alreadyInPlaylist = playlist.tracks.value.any { track -> track.uri == it.uri }
            }
        }

    Column(modifier = Modifier.fillMaxSize()) {
        Header { SearchBar(onChange = { searchValue.tryEmit(it) }) }
        Divider(color = LightGray, thickness = 2.dp)
        Search(tracks = results, modifier = Modifier.weight(1f), onAdd = { playlist.add(it) })
    }
}
