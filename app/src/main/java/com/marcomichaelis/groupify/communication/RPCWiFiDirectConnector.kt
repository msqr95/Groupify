package com.marcomichaelis.groupify.communication

import java.io.Closeable
import kotlin.reflect.KClass

class RPCWiFiDirectConnector constructor(private val host: String, private val port: Int) : WiFiDirectConnector {
    private val url: String
        get() = "http://$host:$port"


    override suspend fun <Return : Any> sendMessage(message: Any, returnType: KClass<Return>): Return {
        TODO("Not yet implemented")
    }

    override suspend fun <Message : Any> requestStream(
        type: String,
        callback: (Message) -> Unit,
        messageType: KClass<Message>
    ): Closeable {
        TODO("Not yet implemented")
    }
}