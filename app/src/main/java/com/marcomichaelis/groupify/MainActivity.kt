package com.marcomichaelis.groupify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.marcomichaelis.groupify.components.theme.GroupifyTheme
import com.marcomichaelis.groupify.spotify.LocalSpotifyContext
import com.marcomichaelis.groupify.spotify.SpotifyContext
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    private val spotifyContext = SpotifyContext()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalSpotifyContext provides spotifyContext) {
                GroupifyTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_background),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = null
                        )
                        Router()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            val response = AuthorizationResponse.fromUri(it)
            spotifyContext.handleAuthorizationResponse(response)
        }
    }
}
