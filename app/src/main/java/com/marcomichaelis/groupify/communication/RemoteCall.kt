package com.marcomichaelis.groupify.communication

import kotlinx.serialization.Serializable

@Serializable
data class RemoteCall(val methodName: String, val parameters: List<String?> = listOf())

fun RemoteCall(methodName: String, vararg parameters: Any?): RemoteCall {
    return RemoteCall(methodName, parameters.map { it?.toString() })
}
