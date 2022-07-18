package com.marcomichaelis.groupify.components

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.R
import com.marcomichaelis.groupify.components.theme.SpotifyGreen
import com.marcomichaelis.groupify.spotify.LocalSpotifyAuthContext
import com.spotify.sdk.android.auth.AuthorizationClient

@Composable
fun SpotifyLoginButton(onLoginStart: () -> Unit) {
    val spotifyContext = LocalSpotifyAuthContext.current
    val activity = LocalContext.current as Activity

    Button(
        modifier = Modifier.testTag("loginButton"),
        onClick = {
            onLoginStart()
            AuthorizationClient.openLoginActivity(
                activity,
                spotifyContext.requestCode,
                spotifyContext.authRequest
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
