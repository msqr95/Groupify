package com.marcomichaelis.groupify.components.playlist

import android.widget.Toast
import androidx.compose.material.Divider
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.components.VerticalList
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.models.Track as TrackModel
import kotlinx.coroutines.launch

@Composable
fun Playlist(
    tracks: List<TrackModel>,
    modifier: Modifier = Modifier,
    onDelete: (TrackModel) -> Unit
) {
    val context = LocalContext.current
    val spotifyClient = LocalSpotifyClientContext.current.client
    val scope = rememberCoroutineScope()

    VerticalList(modifier = modifier) {
        tracks.forEach {
            PlaylistTrack(
                track = it,
                onClick = {
                    if (spotifyClient.type == Type.HOST) {
                        scope.launch {
                            if (spotifyClient.play(it.uri)) {
                                Toast.makeText(context, "Playing track", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Not playing track", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                },
                showDeleteButton = spotifyClient.type == Type.HOST,
                onDelete = { onDelete(it) }
            )
            Divider(color = LightGray, thickness = 1.dp)
        }
    }
}
