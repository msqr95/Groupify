package com.marcomichaelis.groupify.storage

import android.content.Context
import androidx.room.*
import com.marcomichaelis.groupify.spotify.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ListConverter {
    companion object {
        private val json = Json

        @TypeConverter
        @JvmStatic
        fun fromString(value: String): List<String> {
            return json.decodeFromString(value)
        }

        @TypeConverter
        @JvmStatic
        fun fromList(value: List<String>): String {
            return json.encodeToString(value)
        }
    }
}

@Database(entities = [(Track::class)], version = 1)
@TypeConverters(ListConverter::class)
abstract class TrackRoomDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        private var INSTANCE: TrackRoomDatabase? = null

        fun getInstance(context: Context): TrackRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance =
                        Room.databaseBuilder(
                                context.applicationContext,
                                TrackRoomDatabase::class.java,
                                "track_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
