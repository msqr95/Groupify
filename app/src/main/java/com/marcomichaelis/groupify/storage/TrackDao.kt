package com.marcomichaelis.groupify.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.marcomichaelis.groupify.spotify.models.Track

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks LIMIT 1 OFFSET 1")
    suspend fun find(): Track?

    @Insert suspend fun insertTrack(track: Track)

    @Insert suspend fun insertAllTracks(tracks: List<Track>)

    @Query("DELETE FROM tracks") suspend fun clearTracks()

    @Delete suspend fun deleteTrack(track: Track)

    @Query("SELECT * FROM tracks") fun getAllTracks(): LiveData<List<Track>>
}
