package com.marcomichaelis.groupify.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marcomichaelis.groupify.components.GroupList
import com.marcomichaelis.groupify.components.Logo
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.p2p.LocalWifiP2pContext
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.storage.TrackRoomDatabase
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JoinGroupPage(navController: NavController) {
    val context = LocalContext.current
    val wifiP2pContext = LocalWifiP2pContext.current
    val spotifyClientContext = LocalSpotifyClientContext.current
    val groups by wifiP2pContext.groupOwners.collectAsState(listOf())
    val scope = rememberCoroutineScope()
    val trackDao = remember { TrackRoomDatabase.getInstance(context).trackDao() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.padding(16.dp))
        Text(
            text =
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append("There are ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(groups.size.toString())
                        }
                        append(" groups nearby")
                    }
                },
            modifier = Modifier.padding(top = 80.dp, bottom = 34.dp),
            color = LightGray
        )
        GroupList {
            scope.launch {
                spotifyClientContext.constructClient()
                trackDao.clearTracks()
                navController.navigate("playlist")
            }
        }
    }
}
