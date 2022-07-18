package com.marcomichaelis.groupify.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VerticalList(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState), content = content)
}
