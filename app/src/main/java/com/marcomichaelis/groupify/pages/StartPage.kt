package com.marcomichaelis.groupify.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.marcomichaelis.groupify.components.Button
import com.marcomichaelis.groupify.components.Logo
import com.marcomichaelis.groupify.p2p.LocalWifiP2pContext

private val ButtonWidth = 188.dp

@Composable
fun StartPage(navController: NavController) {
    val wifiP2pContext = LocalWifiP2pContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {
                wifiP2pContext.createGroup()
                navController.navigate("spotify-login")
            },
            modifier = Modifier.width(ButtonWidth).padding(top = 75.dp)
        ) { Text(text = "Create new group") }
        Text(text = "or", fontSize = 16.sp, modifier = Modifier.padding(14.dp))
        Button(
            onClick = { navController.navigate("join-group") },
            modifier = Modifier.width(ButtonWidth)
        ) { Text(text = "Join group") }
    }
}
