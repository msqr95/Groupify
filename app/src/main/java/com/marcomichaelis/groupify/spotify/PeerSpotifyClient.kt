package com.marcomichaelis.groupify.spotify

import com.marcomichaelis.groupify.communication.RemoteCall
import com.marcomichaelis.groupify.communication.WiFiDirectConnector
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.models.Device
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import com.marcomichaelis.groupify.spotify.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

class PeerSpotifyClient constructor(private val wiFiDirectConnector: WiFiDirectConnector) :
    SpotifyClient {

    override val type: Type
        get() = Type.PEER

    override suspend fun search(keyword: String): List<Track> = fromJson {
        wiFiDirectConnector.sendRemoteCall(RemoteCall("search", keyword))
    }

    override suspend fun getDevices(): List<Device> = fromJson {
        wiFiDirectConnector.sendRemoteCall(RemoteCall("getDevices"))
    }

    override suspend fun play(trackUri: String?): Boolean = fromJson {
        wiFiDirectConnector.sendRemoteCall(RemoteCall("play", trackUri))
    }

    override suspend fun pause(): Boolean = fromJson {
        wiFiDirectConnector.sendRemoteCall(RemoteCall("pause"))
    }

    override suspend fun setVolume(percent: Int): Boolean = fromJson {
        wiFiDirectConnector.sendRemoteCall(RemoteCall("setVolume", percent))
    }

    override suspend fun streamPlaybackState(callback: suspend (PlaybackState?) -> Unit) {
        wiFiDirectConnector.requestStream("playbackState") { callback(json.decodeFromString(it)) }
    }

    override suspend fun streamPlaylist(callback: suspend (List<Track>) -> Unit) {
        wiFiDirectConnector.requestStream("playlist") { callback(json.decodeFromString(it)) }
    }

    override suspend fun addTrack(track: Track) {
        wiFiDirectConnector.sendRemoteCall(
            RemoteCall("addTrack", json.encodeToString(track))
        )
    }
}

inline fun <reified T : Any> fromJson(block: () -> String): T {
    return json.decodeFromString(block())
}
