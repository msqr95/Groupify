package com.marcomichaelis.groupify.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.p2p.WifiDevice
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val device = WifiDevice("address", "Device Name")

    @Test
    fun testGroupName() {
        composeTestRule.setContent { Group(device = device) {} }

        composeTestRule.onNodeWithTag("groupName", useUnmergedTree = true).assertTextContains("Device Name")
    }

    @Test
    fun testGroupClick() {
        val callback = mockk<() -> Unit>()
        every { callback.invoke() } returns Unit

        composeTestRule.setContent { Group(device = device, callback) }

        composeTestRule.onNodeWithTag("group").performClick()

        verify { callback.invoke() }
    }
}
