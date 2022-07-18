package com.marcomichaelis.groupify.pages

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marcomichaelis.groupify.components.Logo
import com.marcomichaelis.groupify.components.SpotifyLoginButton
import com.marcomichaelis.groupify.components.theme.SpotifyGreen
import com.marcomichaelis.groupify.spotify.LocalSpotifyAuthContext
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.storage.TrackRoomDatabase
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpotifyLoginPage(navController: NavController) {
    val context = LocalContext.current
    val spotifyAuthContext = LocalSpotifyAuthContext.current
    val spotifyClientContext = LocalSpotifyClientContext.current
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val trackDao = remember { TrackRoomDatabase.getInstance(context).trackDao() }

    LaunchedEffect(spotifyAuthContext) {
        spotifyClientContext.constructClient()
        Handler(Looper.getMainLooper()).post { navController.navigate("playlist") }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.padding(16.dp))
        if (!isLoading.value) {
            Text(
                text = "You must login with Spotify before creating a new group",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier.fillMaxWidth(fraction = 0.8f).padding(top = 56.dp, bottom = 34.dp)
            )
            SpotifyLoginButton(
                onLoginStart = {
                    scope.launch {
                        trackDao.clearTracks()
                        isLoading.value = true
                    }
                }
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 60.dp),
                color = SpotifyGreen
            )
            Text(
                text = "Waiting for login to complete...",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(fraction = 0.5f).padding(top = 30.dp)
            )
        }
    }
}
