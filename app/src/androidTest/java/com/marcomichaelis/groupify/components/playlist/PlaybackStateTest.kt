package com.marcomichaelis.groupify.components.playlist

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.spotify.models.PlaybackState as PlaybackStateModel
import com.marcomichaelis.groupify.spotify.models.Track
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val testTrack =
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

@RunWith(AndroidJUnit4::class)
class PlaybackStateTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val playbackState =
        mockk<PlaybackStateModel>(relaxed = true) { every { track } returns testTrack }

    @Test
    fun testPlayingState() {
        val onClick = mockk<(Boolean) -> Unit>()
        every { onClick.invoke(any()) } returns Unit
        every { playbackState.isPlaying } returns true

        composeTestRule.setContent {
            PlaybackState(
                playbackState = playbackState,
                playButtonVisible = true,
                onClick = onClick
            )
        }

        composeTestRule.onNodeWithTag("playButton").performClick()

        verify { onClick.invoke(true) }
    }

    @Test
    fun testNotPlayingState() {
        val onClick = mockk<(Boolean) -> Unit>()
        every { onClick.invoke(any()) } returns Unit
        every { playbackState.isPlaying } returns false

        composeTestRule.setContent {
            PlaybackState(
                playbackState = playbackState,
                playButtonVisible = true,
                onClick = onClick
            )
        }

        composeTestRule.onNodeWithTag("playButton").performClick()

        verify { onClick.invoke(true) }
    }
}
