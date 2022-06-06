package com.marcomichaelis.groupify.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.components.Logo
import com.marcomichaelis.groupify.components.SpotifyLoginButton
import com.marcomichaelis.groupify.components.theme.SpotifyGreen
import com.marcomichaelis.groupify.spotify.LocalSpotifyContext

@Composable
fun SpotifyLoginPage() {
    val spotifyContext = LocalSpotifyContext.current
    val isLoading = remember { mutableStateOf(false) }

    spotifyContext.addCallback { isLoading.value = false }

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
            SpotifyLoginButton(onLoginStart = { isLoading.value = true })
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
