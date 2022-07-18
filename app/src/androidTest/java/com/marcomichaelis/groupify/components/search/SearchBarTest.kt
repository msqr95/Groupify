package com.marcomichaelis.groupify.components.search

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSearchBarEmit() {
        val onChange = mockk<(String) -> Unit>()
        every { onChange.invoke(any()) } returns Unit
        composeTestRule.setContent { SearchBar(onChange = onChange, modifier = Modifier.testTag("searchBar")) }

        composeTestRule.onNodeWithTag("searchBar").performTextInput("search term")

        verify { onChange.invoke("search term") }
    }
}
