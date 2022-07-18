package com.marcomichaelis.groupify.components.playlist

import androidx.compose.runtime.Composable
import com.marcomichaelis.groupify.components.search.SearchButton
import com.marcomichaelis.groupify.components.Header

@Composable
fun PlaylistHeader(onClickSearch: () -> Unit) {
    Header { SearchButton(onClickSearch) }
}
