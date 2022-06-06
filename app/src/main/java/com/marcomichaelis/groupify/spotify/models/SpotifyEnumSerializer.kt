package com.marcomichaelis.groupify.spotify.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class SpotifyEnumSerializer<E : Enum<E>>(
    private val defaultValue: E,
    private val enumValueOf: (String) -> E
) : KSerializer<E> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(defaultValue.declaringClass.name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: E) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): E {
        return try {
            val key = decoder.decodeString()
            enumValueOf("${key[0].uppercase()}${key.substring(1)}")
        } catch (e: IllegalArgumentException) {
            defaultValue
        }
    }
}
