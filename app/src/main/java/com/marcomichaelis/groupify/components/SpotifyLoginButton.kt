package com.marcomichaelis.groupify.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.R
import com.marcomichaelis.groupify.components.theme.SpotifyGreen
import com.marcomichaelis.groupify.spotify.LocalSpotifyContext
import com.spotify.sdk.android.auth.AuthorizationClient

@Composable
fun SpotifyLoginButton(onLoginStart: () -> Unit) {
    val spotifyContext = LocalSpotifyContext.current
    val activity = LocalContext.current as Activity
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            AuthorizationClient.getResponse(it.resultCode, it.data)
                .let(spotifyContext::handleAuthorizationResponse)
        }

    Button(
        onClick = {
            onLoginStart()
            launcher.launch(
                AuthorizationClient.createLoginActivityIntent(activity, spotifyContext.authRequest)
            )
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_spotify_logo),
            modifier = Modifier.size(32.dp).padding(end = 8.dp),
            tint = SpotifyGreen,
            contentDescription = null
        )
        Text(text = "Login with Spotify")
    }
}
