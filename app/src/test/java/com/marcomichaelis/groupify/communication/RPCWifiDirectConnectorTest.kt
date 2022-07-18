package com.marcomichaelis.groupify.communication

import android.content.Context
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotEmpty
import com.marcomichaelis.groupify.spotify.SpotifyClient
import com.marcomichaelis.groupify.spotify.models.Track
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket

val tracks =
    listOf(
        Track(
            id = 1,
            title = "Title",
            uri = "uri",
            albumUri = "uri",
            coverImage = "cover",
            artists = listOf("artist"),
            duration = 3,
            explicit = true
        )
    )

class RPCWifiDirectConnectorTest {

    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var server: RPCWiFiDirectServer
    private val context = mockk<Context>()
    private val streamPlaylist = slot<suspend (List<Track>) -> Unit>()
    private val hostSpotifyClient =
        mockk<SpotifyClient>(relaxed = true) {
            coEvery { streamPlaylist(capture(streamPlaylist)) } returns Unit
        }

    @Before
    fun before() {
        val freePort =
            ServerSocket(0).use {
                val port = it.localPort
                it.close()
                port
            }
        server = RPCWiFiDirectServer(hostSpotifyClient, port = freePort, context).apply { start() }
    }

    @Test
    fun `should do a remote call`(): Unit = runBlocking {
        coEvery { hostSpotifyClient.search(any()) } returns tracks
        val connector = RPCWiFiDirectConnector(host = "localhost", server.port)

        val response = connector.sendRemoteCall(RemoteCall("search", listOf("keyword")))
        assertThat(response).isNotEmpty()

        val tracks = json.decodeFromString<List<Track>>(response)
        assertThat(tracks).hasSize(1)
    }

    @Test
    fun `should stream updates`(): Unit = runBlocking {
        val connector = RPCWiFiDirectConnector(host = "localhost", server.port)
        val callback = mockk<suspend (String) -> Unit>()

        connector.requestStream("playlist", callback)
        delay(500)
        streamPlaylist.coInvoke(tracks)

        coVerify(timeout = 500) { callback.invoke(json.encodeToString(tracks)) }
    }
}
