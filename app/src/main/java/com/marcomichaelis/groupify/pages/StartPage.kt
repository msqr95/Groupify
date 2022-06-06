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

private val ButtonWidth = 188.dp

@Composable
fun StartPage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(modifier = Modifier.padding(16.dp))
        Button(
            onClick = { navController.navigate("spotify-login") },
            modifier = Modifier.width(ButtonWidth).padding(top = 75.dp)
        ) { Text(text = "Create new group") }
        Text(text = "or", fontSize = 16.sp, modifier = Modifier.padding(14.dp))
        Button(onClick = { /*TODO*/}, modifier = Modifier.width(ButtonWidth)) {
            Text(text = "Join group")
        }
    }
}
