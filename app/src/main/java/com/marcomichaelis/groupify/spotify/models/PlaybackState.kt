package com.marcomichaelis.groupify.spotify.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable(with = RepeatStateSerializer::class)
enum class RepeatState {
    Off,
    Track,
    Context
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = RepeatState::class)
class RepeatStateSerializer :
    SpotifyEnumSerializer<RepeatState>(RepeatState.Off, RepeatState::valueOf)

@Serializable(with = ShuffleStateSerializer::class)
enum class ShuffleState {
    Off,
    On
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ShuffleState::class)
class ShuffleStateSerializer :
    SpotifyEnumSerializer<ShuffleState>(ShuffleState.Off, ShuffleState::valueOf)

@Serializable
data class PlaybackState(
    @SerialName("item") val track: Track,
    val device: Device,
    @SerialName("is_playing") val isPlaying: Boolean,
    val progress: Int?,
    @SerialName("repeat_state") val repeatState: String, // TODO Add Enum
    @SerialName("shuffle_state") val shuffleState: String, // TODO Add Enum
)
