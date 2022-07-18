package com.marcomichaelis.groupify.communication

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionInfo(val topic: String)
