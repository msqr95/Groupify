package com.marcomichaelis.groupify.components.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val ColorPalette = darkColors(
    primary = DefaultDark,
    primaryVariant = Lighter,
    secondary = LightGray,
    background = Background
)

@Composable
fun GroupifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}