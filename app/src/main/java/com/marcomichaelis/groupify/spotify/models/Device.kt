package com.marcomichaelis.groupify.spotify.models

import kotlinx.serialization.*

@Serializable(with = DeviceTypeSerializer::class)
enum class DeviceType {
    Computer,
    Smartphone,
    Speaker,
    Unknown
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DeviceType::class)
class DeviceTypeSerializer :
    SpotifyEnumSerializer<DeviceType>(DeviceType.Unknown, DeviceType::valueOf)

@Serializable
data class Device(
    val id: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_restricted") val isRestricted: Boolean,
    val name: String,
    val type: DeviceType
)
