package com.marcomichaelis.groupify.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marcomichaelis.groupify.MainActivity
import com.marcomichaelis.groupify.p2p.LocalWifiP2pContext
import com.marcomichaelis.groupify.p2p.WifiDevice
import com.marcomichaelis.groupify.p2p.WifiP2pContext
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupListTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mockGroupOwners =
        MutableSharedFlow<List<WifiDevice>>(replay = 1).apply {
            tryEmit(listOf(WifiDevice(deviceAddress = "address", deviceName = "name")))
        }

    private val connectCallback = slot<(Boolean) -> Unit>()
    private val wifiP2pContext =
        mockk<WifiP2pContext> {
            every { groupOwners } returns mockGroupOwners
            every { connect(any(), capture(connectCallback)) } returns Unit
        }

    @Test
    fun testGroupList() {
        val fn = mockk<() -> Unit>()
        every { fn.invoke() } returns Unit

        composeTestRule.setContent {
            CompositionLocalProvider(LocalWifiP2pContext provides wifiP2pContext) {
                GroupList(onGroupSelected = { fn.invoke() })
            }
        }

        composeTestRule.onAllNodesWithTag("group").assertCountEquals(1)
        composeTestRule.onAllNodesWithTag("group")[0].performClick()
        connectCallback.invoke(true)

        verify { fn.invoke() }
    }
}
