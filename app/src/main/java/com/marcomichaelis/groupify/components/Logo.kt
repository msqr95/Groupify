package com.marcomichaelis.groupify.components

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.marcomichaelis.groupify.R

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = null
    )
}
