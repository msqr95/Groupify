package com.marcomichaelis.groupify.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marcomichaelis.groupify.components.playlist.PlaybackState
import com.marcomichaelis.groupify.components.playlist.Playlist
import com.marcomichaelis.groupify.components.playlist.PlaylistHeader
import com.marcomichaelis.groupify.components.rememberPlaylist
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import kotlinx.coroutines.launch

@Composable
fun PlaylistPage(navController: NavController) {
    val spotifyClient = LocalSpotifyClientContext.current.client
    val playbackState = remember { mutableStateOf<PlaybackState?>(null) }
    val playlist = rememberPlaylist()
    val scope = rememberCoroutineScope()

    LaunchedEffect(spotifyClient) {
        spotifyClient.streamPlaybackState {
            playbackState.value = it

            // continue to next track if last one is done
            if ((it == null) or (it?.isPlaying == false && it.progress == 0)) {
                playlist.getNext()?.let { next ->
                    spotifyClient.play(next.uri)
                    val dbTrack = playlist.tracks.value.firstOrNull()
                    if (dbTrack != null) {
                        playlist.delete(dbTrack)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PlaylistHeader(onClickSearch = { navController.navigate("search") })
        Divider(color = LightGray, thickness = 2.dp)
        Playlist(
            tracks = playlist.tracks.value,
            modifier = Modifier.weight(1f),
            onDelete = { playlist.delete(it) }
        )
        Divider(color = Color.LightGray, thickness = 2.dp)
        PlaybackState(
            playbackState = playbackState.value,
            playButtonVisible = spotifyClient.type == Type.HOST
        ) { playing ->
            scope.launch {
                if (playing) {
                    spotifyClient.pause()
                } else {
                    spotifyClient.play()
                }
            }
        }
    }
}
