package com.marcomichaelis.groupify.spotify

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.compositionLocalOf
import com.marcomichaelis.groupify.communication.RPCWiFiDirectConnector
import com.marcomichaelis.groupify.communication.RPCWiFiDirectServer
import com.marcomichaelis.groupify.communication.WiFiDirectConnector
import com.marcomichaelis.groupify.communication.WiFiDirectServer
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.p2p.WifiP2pContext
import kotlinx.coroutines.flow.*

val LocalSpotifyClientContext =
    compositionLocalOf<SpotifyClientContext> { throw Exception("missing context in scope") }

private const val Port = 45454

class SpotifyClientContext(
    private val spotifyAuthContext: SpotifyAuthContext,
    private val wifiP2pContext: WifiP2pContext,
    private val context: Context
) {

    lateinit var client: SpotifyClient
        private set
    private var server: WiFiDirectServer? = null
    private var connector: WiFiDirectConnector? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun constructClient() {
        when (wifiP2pContext.type) {
            Type.HOST -> {
                spotifyAuthContext.authTokens.take(1).collect {
                    client = HostSpotifyClient(it)
                    server =
                        RPCWiFiDirectServer(client as HostSpotifyClient, port = Port, context)
                            .apply { start() }
                }
            }
            Type.PEER -> {
                combine(
                        wifiP2pContext.groupOwnerAddress.take(1),
                        wifiP2pContext.groupOwners.take(1)
                    ) { address, _ -> address }
                    .collect { address ->
                        connector =
                            RPCWiFiDirectConnector(address.hostAddress ?: address.hostName, Port)
                        client = PeerSpotifyClient(connector!!)
                    }
            }
            Type.UNKNOWN -> {
                throw Exception("Type is still unknown.")
            }
        }
    }

    fun destroy() {
        server?.stop()
    }
}
