package com.marcomichaelis.groupify.components.search

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SearchBar(modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    val term = remember { mutableStateOf("") }

    TextField(
        value = term.value,
        onValueChange = {
            term.value = it
            onChange(it)
        },
        modifier = modifier,
        singleLine = true,
        colors =
            TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
            ),
        trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
        placeholder = { Text(text = "Search...") }
    )
}
