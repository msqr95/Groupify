package com.marcomichaelis.groupify.communication

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketMessage(val topic: String, val message: String)

