package com.marcomichaelis.groupify.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.spotify.LocalSpotifyAuthContext
import com.marcomichaelis.groupify.spotify.SpotifyAuthContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpotifyLoginButtonTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val spotifyAuthContext = mockk<SpotifyAuthContext>(relaxed = true)

    @Test
    fun testLoginButton() {
        val callback = mockk<() -> Unit>()
        every { callback.invoke() } returns Unit

        composeTestRule.setContent {
            CompositionLocalProvider(LocalSpotifyAuthContext provides spotifyAuthContext) {
                SpotifyLoginButton(callback)
            }
        }

        composeTestRule.onNodeWithTag("loginButton").performClick()

        verify { callback.invoke() }
    }
}
