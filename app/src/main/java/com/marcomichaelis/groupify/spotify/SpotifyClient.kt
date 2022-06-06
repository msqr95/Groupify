package com.marcomichaelis.groupify.spotify

import com.marcomichaelis.groupify.spotify.models.Device
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import com.marcomichaelis.groupify.spotify.models.Track

interface SpotifyClient {
    suspend fun search(keyword: String): List<Track>
    suspend fun getDevices(): List<Device>
    suspend fun play(trackUri: String)
    suspend fun pause()
    suspend fun setVolume()
    suspend fun streamPlaybackState(callback: (PlaybackState) -> Unit)
}
