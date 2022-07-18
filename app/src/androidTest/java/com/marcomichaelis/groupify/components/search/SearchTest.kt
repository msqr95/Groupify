package com.marcomichaelis.groupify.components.search

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.spotify.models.DefaultCoverImage
import com.marcomichaelis.groupify.spotify.models.Track
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
class SearchTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSearchAdd() {
        val onAdd = mockk<(Track) -> Unit>()
        every { onAdd.invoke(any()) } returns Unit

        composeTestRule.setContent { Search(tracks = tracks, onAdd = onAdd) }

        composeTestRule.onAllNodesWithTag("searchTrack").assertCountEquals(3)
        composeTestRule.onAllNodesWithTag("searchTrackAdd")[0].performClick()

        verify { onAdd.invoke(tracks[0]) }
    }
}
