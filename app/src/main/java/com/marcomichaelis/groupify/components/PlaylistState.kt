package com.marcomichaelis.groupify.components

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.HostSpotifyClient
import com.marcomichaelis.groupify.spotify.LocalSpotifyClientContext
import com.marcomichaelis.groupify.spotify.models.Track
import com.marcomichaelis.groupify.storage.TrackRoomDatabase
import kotlinx.coroutines.launch

interface PlaylistState {
    val tracks: State<List<Track>>
    fun add(track: Track)
    fun delete(track: Track)
    suspend fun getNext(): Track?
}

@Composable
fun rememberPlaylist(): PlaylistState {
    val context = LocalContext.current
    val spotifyClient = LocalSpotifyClientContext.current.client
    val scope = rememberCoroutineScope()
    val trackDao = remember { TrackRoomDatabase.getInstance(context).trackDao() }
    val tracks = trackDao.getAllTracks().observeAsState(listOf())

    DisposableEffect(trackDao) {
        var observer: Observer<List<Track>>? = null
        if (spotifyClient.type == Type.HOST) {
            observer =
                Observer<List<Track>> {
                    scope.launch { (spotifyClient as HostSpotifyClient).pushPlaylistChange(it) }
                }
            trackDao.getAllTracks().observeForever(observer)
        } else {
            scope.launch {
                spotifyClient.streamPlaylist {
                    trackDao.clearTracks()
                    trackDao.insertAllTracks(it)
                }
            }
        }

        onDispose { observer?.let { trackDao.getAllTracks().removeObserver(it) } }
    }

    return object : PlaylistState {
        override val tracks: State<List<Track>> = tracks

        override fun delete(track: Track) {
            if (spotifyClient.type == Type.HOST) {
                scope.launch { trackDao.deleteTrack(track) }
            } else {
                throw Exception("Method shouldn't be called by peer")
            }
        }

        override suspend fun getNext(): Track? {
            return trackDao.find()
        }

        override fun add(track: Track) {
            scope.launch {
                if (spotifyClient.type == Type.HOST) {
                    trackDao.insertTrack(track)
                } else {
                    spotifyClient.addTrack(track)
                    track.alreadyInPlaylist = true
                }
            }
        }
    }
}
