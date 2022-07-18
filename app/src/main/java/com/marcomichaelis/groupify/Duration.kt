package com.marcomichaelis.groupify

import kotlin.math.floor

fun formatDuration(durationMs: Int): String {
    val seconds = floor(durationMs.toFloat() / 1000).toInt()
    return String.format("%d:%02d", seconds / 60, seconds % 60)
}

fun formatProgress(duration: Int, progress: Int): String {
    return "${formatDuration(progress)}/${formatDuration(duration)}"
}
