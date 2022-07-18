package com.marcomichaelis.groupify.spotify.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

const val DefaultCoverImage =
    "https://i.scdn.co/image/ab67616d0000b2739510c262f77afc125df888b8"

@Serializable
@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val uri: String,
    val albumUri: String,
    val coverImage: String,
    val artists: List<String>,
    val duration: Long,
    val explicit: Boolean,
) {
    @Ignore var alreadyInPlaylist: Boolean = true

    companion object {
        fun listFromJson(element: JsonElement): List<Track> {
            val items = element.jsonObject["tracks"]?.jsonObject?.get("items") ?: return emptyList()
            return items.jsonArray.map(this::fromJson)
        }

        fun fromJson(it: JsonElement): Track {
            if (it is JsonNull) {
                return Track(
                    title = "",
                    uri = "",
                    albumUri = "",
                    duration = 0,
                    explicit = false,
                    artists = emptyList(),
                    coverImage = DefaultCoverImage,
                )
            }

            val artists =
                it.jsonObject["artists"]?.jsonArray?.map { artist ->
                    artist.jsonObject["name"]?.jsonPrimitive?.content ?: ""
                }
                    ?: emptyList()
            val coverImage =
                it.jsonObject["album"]
                    ?.jsonObject
                    ?.get("images")
                    ?.jsonArray
                    ?.get(0)
                    ?.jsonObject
                    ?.get("url")
                    ?.jsonPrimitive
                    ?.content
                    ?: DefaultCoverImage

            val albumUri =
                it.jsonObject["album"]?.jsonObject?.get("uri")?.jsonPrimitive?.content ?: ""
            return Track(
                uri = it.jsonObject["uri"]?.jsonPrimitive?.content ?: "",
                albumUri = albumUri,
                title = it.jsonObject["name"]?.jsonPrimitive?.content ?: "",
                duration = it.jsonObject["duration_ms"]?.jsonPrimitive?.long ?: 0,
                explicit = it.jsonObject["explicit"]?.jsonPrimitive?.boolean ?: false,
                artists = artists,
                coverImage = coverImage
            )
        }
    }
}
