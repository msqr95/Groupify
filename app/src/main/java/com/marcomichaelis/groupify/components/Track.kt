package com.marcomichaelis.groupify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.formatDuration
import com.marcomichaelis.groupify.spotify.models.Track as TrackModel

@Composable
fun Track(
    modifier: Modifier = Modifier,
    track: TrackModel,
    onClick: () -> Unit,
    scope: @Composable () -> Unit = {}
) {
    Row(
        modifier =
            modifier.background(Color.White.copy(alpha = 0.1f))
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = track.coverImage,
                contentDescription = "Cover image",
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(3.dp))
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(0.dp),
                    fontSize = 16.sp
                )
                Text(text = track.artists.joinToString(), fontSize = 14.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatDuration(track.duration.toInt()),
                color = LightGray.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            scope()
        }
    }
}
