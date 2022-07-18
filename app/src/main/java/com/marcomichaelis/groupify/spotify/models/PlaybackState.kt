package com.marcomichaelis.groupify.spotify.models

import com.marcomichaelis.groupify.spotify.json
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonNull

@Serializable(with = RepeatStateSerializer::class)
enum class RepeatState {
    Off,
    Track,
    Context
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = RepeatState::class)
class RepeatStateSerializer : KSerializer<RepeatState> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RepeatState", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RepeatState) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): RepeatState {
        return try {
            val key = decoder.decodeString()
            enumValueOf("${key[0].uppercase()}${key.substring(1)}")
        } catch (e: IllegalArgumentException) {
            RepeatState.Off
        }
    }
}

@Serializable
data class PlaybackState(
    @SerialName("item") val _track: JsonElement,
    val device: Device,
    @SerialName("is_playing") val isPlaying: Boolean,
    @SerialName("progress_ms") val progress: Int?,
    @SerialName("repeat_state") val repeatState: RepeatState,
    @SerialName("shuffle_state") val shuffleState: Boolean,
) {
    val track by lazy { Track.fromJson(_track) }
}

