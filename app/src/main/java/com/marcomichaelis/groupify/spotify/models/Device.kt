package com.marcomichaelis.groupify.spotify.models

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DeviceTypeSerializer::class)
enum class DeviceType {
    Computer,
    Smartphone,
    Speaker,
    Unknown
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DeviceType::class)
class DeviceTypeSerializer : KSerializer<DeviceType> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DeviceType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DeviceType) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): DeviceType {
        return try {
            val key = decoder.decodeString()
            enumValueOf("${key[0].uppercase()}${key.substring(1)}")
        } catch (e: IllegalArgumentException) {
            DeviceType.Unknown
        }
    }
}

@Serializable
data class Device(
    val id: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_restricted") val isRestricted: Boolean,
    val name: String,
    val type: String,
)
