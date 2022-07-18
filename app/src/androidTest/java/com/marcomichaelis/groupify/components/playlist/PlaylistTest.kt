package com.marcomichaelis.groupify.components.playlist

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.components.search.track
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.SpotifyClient
import com.marcomichaelis.groupify.spotify.SpotifyClientContext
import com.marcomichaelis.groupify.spotify.models.DefaultCoverImage
import com.marcomichaelis.groupify.spotify.models.Track
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val tracks =
    (1..3)
        .map {
            Track(
                id = it,
                title = "Title",
                uri = "uri",
                albumUri = "uri",
                coverImage = DefaultCoverImage,
                artists = listOf("artist"),
                duration = 3,
                explicit = true,
            ).apply { alreadyInPlaylist = false }
        }
        .toList()

@RunWith(AndroidJUnit4::class)
class PlaylistTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val spotifyClient = mockk<SpotifyClient>(relaxed = true)
    private val spotifyClientContext =
        mockk<SpotifyClientContext> { every { client } returns spotifyClient }

    @Test
    fun testPlaylistTrackClick() {
        every { spotifyClient.type } returns Type.HOST

        composeTestRule.setContent {
            CompositionLocalProvider(LocalSpotifyClientContext provides spotifyClientContext) {
                Playlist(tracks = tracks, onDelete = {})
            }
        }

        composeTestRule.onAllNodesWithTag("playlistTrack").assertCountEquals(3)
        composeTestRule.onAllNodesWithTag("playlistTrack")[0].performClick()

        coVerify { spotifyClient.play(any()) }
    }

    @Test
    fun testPlaylistTrackClickNotHost() {
        every { spotifyClient.type } returns Type.PEER

        composeTestRule.setContent {
            CompositionLocalProvider(LocalSpotifyClientContext provides spotifyClientContext) {
                Playlist(tracks = tracks, onDelete = {})
            }
        }

        composeTestRule.onAllNodesWithTag("playlistTrack")[0].performClick()

        coVerify(exactly = 0) { spotifyClient.play(any()) }
    }

    @Test
    fun testPlaylistTrackDelete() {
        every { spotifyClient.type } returns Type.HOST
        val onDelete = mockk<(Track) -> Unit>()
        every { onDelete.invoke(any()) } returns Unit

        composeTestRule.setContent {
            CompositionLocalProvider(LocalSpotifyClientContext provides spotifyClientContext) {
                Playlist(tracks = tracks, onDelete = onDelete)
            }
        }

        composeTestRule.onAllNodesWithTag("playlistTrackDelete")[0].performClick()

        verify { onDelete.invoke(tracks[0]) }
    }
}
