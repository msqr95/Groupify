package com.marcomichaelis.groupify.spotify

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.p2p.WifiDevice
import com.marcomichaelis.groupify.p2p.WifiP2pContext
import io.mockk.every
import io.mockk.mockk
import java.net.InetAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SpotifyClientContextTest {

    private val wifiP2pContext = mockk<WifiP2pContext>()
    private val spotifyAuthContext = mockk<SpotifyAuthContext>()
    private val spotifyClientContext =
        SpotifyClientContext(spotifyAuthContext, wifiP2pContext, mockk(relaxed = true))

    @Test
    fun `should construct host clients for type HOST`(): Unit = runBlocking {
        every { wifiP2pContext.type } returns Type.HOST
        every { spotifyAuthContext.authTokens } returns
            MutableSharedFlow<AuthTokens>(replay = 1).apply {
                tryEmit(AuthTokens("access", 10, "refresh"))
            }

        spotifyClientContext.constructClient()
        assertThat(spotifyClientContext.client).isInstanceOf(HostSpotifyClient::class)
    }

    @Test
    fun `should construct peer clients for type PEER`(): Unit = runBlocking {
        every { wifiP2pContext.type } returns Type.PEER
        every { wifiP2pContext.groupOwners } returns
            MutableSharedFlow<List<WifiDevice>>(replay = 1).apply { tryEmit(listOf()) }
        every { wifiP2pContext.groupOwnerAddress } returns
            MutableSharedFlow<InetAddress>(replay = 1).apply { tryEmit(InetAddress.getLocalHost()) }

        spotifyClientContext.constructClient()
        assertThat(spotifyClientContext.client).isInstanceOf(PeerSpotifyClient::class)
    }

    @Test
    fun `should raise an exception if type is not set`(): Unit = runBlocking {
        every { wifiP2pContext.type } returns Type.UNKNOWN

        assertThat { spotifyClientContext.constructClient() }.isFailure()
    }


}
