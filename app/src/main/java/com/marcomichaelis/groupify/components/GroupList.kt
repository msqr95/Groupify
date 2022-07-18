package com.marcomichaelis.groupify.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.components.theme.DefaultDark
import com.marcomichaelis.groupify.components.theme.Shapes
import com.marcomichaelis.groupify.p2p.LocalWifiP2pContext
import kotlinx.coroutines.launch

@Composable
fun GroupList(modifier: Modifier = Modifier, onGroupSelected: () -> Unit) {
    val wifiP2pContext = LocalWifiP2pContext.current
    val scrollState = rememberScrollState()
    val groupOwners = wifiP2pContext.groupOwners.collectAsState(listOf())
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            modifier
                .defaultMinSize(minWidth = 250.dp)
                .height(218.dp)
                .border(width = 2.dp, color = DefaultDark, shape = Shapes.small)
                .verticalScroll(scrollState)
                .padding(3.dp)
    ) {
        groupOwners.value.forEach {
            Group(it) {
                wifiP2pContext.connect(it) { success ->
                    if (success) {
                        println("Connect success")
                        onGroupSelected()
                    } else {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Couldn't join group")
                        }
                    }
                }
            }
        }
    }
}
