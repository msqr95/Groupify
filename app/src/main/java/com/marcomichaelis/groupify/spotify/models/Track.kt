package com.marcomichaelis.groupify.spotify.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

private const val DefaultCoverImage =
    "https://i.scdn.co/image/ab67616d0000b2739510c262f77afc125df888b8"

@Serializable(with = TrackSerializer::class)
data class Track(
    val title: String,
    val uri: String,
    val albumUri: String,
    val coverImage: String,
    val artists: List<String>,
    val duration: Long,
    val explicit: Boolean
) {
    companion object {
        fun listFromJson(element: JsonElement): List<Track> {
            val items = element.jsonObject["tracks"]?.jsonObject?.get("items") ?: return emptyList()
            return items.jsonArray.map(this::fromJson)
        }

        fun fromJson(it: JsonElement): Track {
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Track::class)
object TrackSerializer : KSerializer<Track> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Track")

    override fun serialize(encoder: Encoder, value: Track) {
        throw NotImplementedError("Encoding tracks is not supported")
    }

    override fun deserialize(decoder: Decoder): Track {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return Track.fromJson(element)
    }
}
