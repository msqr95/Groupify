package com.marcomichaelis.groupify.spotify

import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.models.Device
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import com.marcomichaelis.groupify.spotify.models.Track

interface SpotifyClient {
    suspend fun search(keyword: String): List<Track>
    suspend fun getDevices(): List<Device>
    suspend fun play(trackUri: String? = null): Boolean
    suspend fun pause(): Boolean
    suspend fun setVolume(percent: Int): Boolean
    suspend fun streamPlaybackState(callback: suspend (PlaybackState?) -> Unit)
    suspend fun streamPlaylist(callback: suspend (List<Track>) -> Unit)
    suspend fun addTrack(track: Track)
    val type: Type
}
