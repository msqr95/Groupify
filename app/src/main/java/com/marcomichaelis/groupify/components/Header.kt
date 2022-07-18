package com.marcomichaelis.groupify.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceBetween,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(modifier = Modifier.size(50.dp).padding(end = 10.dp))
        content()
    }
}
