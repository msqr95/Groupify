package com.marcomichaelis.groupify.components.search

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.spotify.models.DefaultCoverImage
import com.marcomichaelis.groupify.spotify.models.Track
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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

@RunWith(AndroidJUnit4::class)
class SearchTrackTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSearchTrackAddVisible() {
        composeTestRule.setContent {
            SearchTrack(track = track.copy().apply { alreadyInPlaylist = false }) {}
        }

        composeTestRule.onNodeWithContentDescription("Add").assertExists()
    }

    @Test
    fun testSearchTrackTickVisible() {
        composeTestRule.setContent {
            SearchTrack(track = track.copy().apply { alreadyInPlaylist = true }) {}
        }

        composeTestRule.onNodeWithContentDescription("Already in playlist       ").assertExists()
    }
}
