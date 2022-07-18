package com.marcomichaelis.groupify.components.playlist

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.formatProgress
import com.marcomichaelis.groupify.spotify.models.PlaybackState as PlaybackStateModel

@Composable
fun PlaybackState(
    playbackState: PlaybackStateModel?,
    playButtonVisible: Boolean,
    onClick: (playing: Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)){
            AsyncImage(
                model = playbackState?.track?.coverImage,
                contentDescription = "Cover Image",
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = playbackState?.track?.title ?: "",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(0.dp),
                    fontSize = 16.sp
                )
                Text(text = playbackState?.track?.artists?.joinToString() ?: "", fontSize = 14.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text =
                    if (playbackState != null)
                        formatProgress(
                            playbackState.track.duration.toInt(),
                            playbackState.progress ?: 0
                        )
                    else "",
                color = LightGray.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            if (playButtonVisible) {
                IconButton(
                    modifier = Modifier.testTag("playButton"),
                    onClick = { onClick(playbackState != null && playbackState.isPlaying) }
                ) {
                    Icon(
                        imageVector =
                            if (playbackState != null && playbackState.isPlaying)
                                Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
