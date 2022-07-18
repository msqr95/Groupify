package com.marcomichaelis.groupify.communication

interface WiFiDirectConnector {
    suspend fun sendRemoteCall(remoteCall: RemoteCall): String
    suspend fun requestStream(
        topic: String,
        callback: suspend (String) -> Unit,
    )
}
