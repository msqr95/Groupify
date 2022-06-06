package com.marcomichaelis.groupify.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AuthTokens(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("refresh_token")val refreshToken: String,
    //@Transient val requestTime: Long = System.currentTimeMillis()
)


