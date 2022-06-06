package com.marcomichaelis.groupify.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button as MaterialButton
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marcomichaelis.groupify.components.theme.DefaultDark
import com.marcomichaelis.groupify.components.theme.Gray
import com.marcomichaelis.groupify.components.theme.LightGray

enum class ButtonKind constructor(val backgroundColor: Color, val foregroundColor: Color) {
    Primary(DefaultDark, LightGray),
    Secondary(LightGray, DefaultDark),
}

enum class ButtonSize {
    Small,
    Default,
    Large
}

@Composable
fun Button(
    modifier: Modifier = Modifier,
    kind: ButtonKind = ButtonKind.Primary,
    enabled: Boolean = true,
    // size: ButtonSize = ButtonSize.Default,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    MaterialButton(
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = kind.backgroundColor,
                contentColor = kind.foregroundColor,
                disabledBackgroundColor = LightGray,
                disabledContentColor = Gray
            ),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        onClick = onClick,
        content = content,
    )
}
