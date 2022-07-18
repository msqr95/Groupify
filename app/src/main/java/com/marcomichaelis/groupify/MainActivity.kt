package com.marcomichaelis.groupify

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.marcomichaelis.groupify.components.theme.GroupifyTheme
import com.marcomichaelis.groupify.p2p.LocalWifiP2pContext
import com.marcomichaelis.groupify.p2p.WifiP2pContext
import com.marcomichaelis.groupify.spotify.LocalSpotifyAuthContext
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.SpotifyAuthContext
import com.marcomichaelis.groupify.spotify.SpotifyClientContext
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    private val spotifyAuthContext = SpotifyAuthContext()
    private val wifiP2pManager: WifiP2pManager by
        lazy(LazyThreadSafetyMode.NONE) {
            getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        }
    private val wifiP2pContext = WifiP2pContext()
    private val spotifyClientContext = SpotifyClientContext(spotifyAuthContext, wifiP2pContext, this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()

        wifiP2pContext.initialize(this, wifiP2pManager)

        setContent {
            CompositionLocalProvider(
                LocalSpotifyAuthContext provides spotifyAuthContext,
                LocalWifiP2pContext provides wifiP2pContext,
                LocalSpotifyClientContext provides spotifyClientContext
            ) {
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

    private fun requestPermissions() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(
                            this,
                            "Not allowing the permission will result in errors",
                            Toast.LENGTH_LONG
                        )
                        .show()
                } else {
                    println("Permission is granted")
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_WIFI_STATE)
    }

    override fun onResume() {
        super.onResume()
        wifiP2pContext.registerReceiver(this)
    }

    override fun onPause() {
        super.onPause()
        wifiP2pContext.unregisterReceiver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        spotifyClientContext.destroy()
        //wifiP2pContext.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == spotifyAuthContext.requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            spotifyAuthContext.handleAuthorizationResponse(response)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            val response = AuthorizationResponse.fromUri(it)
            spotifyAuthContext.handleAuthorizationResponse(response)
        }
    }
}
