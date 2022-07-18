package com.marcomichaelis.groupify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcomichaelis.groupify.R
import com.marcomichaelis.groupify.components.theme.Gray
import com.marcomichaelis.groupify.components.theme.LightGray
import com.marcomichaelis.groupify.components.theme.Shapes
import com.marcomichaelis.groupify.p2p.WifiDevice

@Composable
fun Group(device: WifiDevice, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier.testTag("group")
                .width(250.dp)
                .clickable { onClick() }
                .border(width = 1.dp, color = Color.Gray, shape = Shapes.small)
                .background(Gray.copy(alpha = 0.12f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_group),
            modifier = Modifier.height(14.dp),
            tint = Gray,
            contentDescription = "Group"
        )
        Text(
            text = device.deviceName,
            fontWeight = FontWeight.SemiBold,
            color = LightGray,
            fontSize = 14.sp,
            modifier = Modifier.testTag("groupName").padding(start = 18.dp)
        )
    }
}
