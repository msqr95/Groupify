package com.marcomichaelis.groupify.spotify

import com.marcomichaelis.groupify.communication.WiFiDirectConnector
import com.marcomichaelis.groupify.spotify.models.Device
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import com.marcomichaelis.groupify.spotify.models.Track

class PeerSpotifyClient constructor(private val wiFiDirectConnector: WiFiDirectConnector) : SpotifyClient {
    override suspend fun search(keyword: String): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getDevices(): List<Device> {
        TODO("Not yet implemented")
    }

    override suspend fun play(trackUri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun pause() {
        TODO("Not yet implemented")
    }

    override suspend fun setVolume() {
        TODO("Not yet implemented")
    }

    override suspend fun streamPlaybackState(callback: (PlaybackState) -> Unit) {
        TODO("Not yet implemented")
    }
}