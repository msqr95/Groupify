package com.marcomichaelis.groupify.spotify

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.marcomichaelis.groupify.communication.RemoteCall
import com.marcomichaelis.groupify.communication.WiFiDirectConnector
import com.marcomichaelis.groupify.spotify.models.DefaultCoverImage
import com.marcomichaelis.groupify.spotify.models.Track
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class PeerSpotifyClientTest {

    private val wifiDirectConnector = mockk<WiFiDirectConnector>()
    private val spotifyClient = PeerSpotifyClient(wifiDirectConnector)
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `should do pause call`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns "false"

        val response = spotifyClient.pause()

        coVerify { wifiDirectConnector.sendRemoteCall(RemoteCall("pause")) }
        assertThat(response).isFalse()
    }
    @Test
    fun `should do setVolume call`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns "true"

        val response = spotifyClient.setVolume(42)

        coVerify { wifiDirectConnector.sendRemoteCall(RemoteCall("setVolume", listOf("42"))) }
        assertThat(response).isTrue()
    }

    @Test
    fun `should do addTrack call`(): Unit = runBlocking {
        val track =
            Track(
                id = 1,
                title = "Title",
                uri = "uri",
                albumUri = "uri",
                coverImage = DefaultCoverImage,
                artists = listOf("artist"),
                duration = 3,
                explicit = true,
            )
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns ""
        spotifyClient.addTrack(track)
        coVerify {
            wifiDirectConnector.sendRemoteCall(RemoteCall("addTrack", json.encodeToString(track)))
        }
    }

    @Test
    fun `should do search remote call`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns "[]"

        val response = spotifyClient.search("keyword")

        coVerify { wifiDirectConnector.sendRemoteCall(RemoteCall("search", listOf("keyword"))) }
        assertThat(response).hasSize(0)
    }

    @Test
    fun `should do get device remote call`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns "[]"

        val response = spotifyClient.getDevices()

        coVerify { wifiDirectConnector.sendRemoteCall(RemoteCall("getDevices")) }
        assertThat(response).hasSize(0)
    }

    @Test
    fun `should do play remote call`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.sendRemoteCall(any()) } returns "true"

        val response = spotifyClient.play("trackUri")

        coVerify { wifiDirectConnector.sendRemoteCall(RemoteCall("play", listOf("trackUri"))) }
        assertThat(response).isTrue()
    }

    @Test
    fun `should subscribe to playlist updates`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.requestStream(any(), any()) } returns Unit

        spotifyClient.streamPlaylist {}

        coVerify { wifiDirectConnector.requestStream("playlist", any()) }
    }

    @Test
    fun `should subscribe to playback updates`(): Unit = runBlocking {
        coEvery { wifiDirectConnector.requestStream(any(), any()) } returns Unit

        spotifyClient.streamPlaybackState {}

        coVerify { wifiDirectConnector.requestStream("playbackState", any()) }
    }
}
