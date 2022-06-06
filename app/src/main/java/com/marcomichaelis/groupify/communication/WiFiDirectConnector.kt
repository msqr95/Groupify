package com.marcomichaelis.groupify.communication

import java.io.Closeable
import kotlin.reflect.KClass

interface WiFiDirectConnector {
    suspend fun <Return : Any> sendMessage(message: Any, returnType: KClass<Return>): Return
    suspend fun <Message : Any> requestStream(
        type: String,
        callback: (Message) -> Unit,
        messageType: KClass<Message>
    ): Closeable
}

suspend inline fun <reified Return : Any> WiFiDirectConnector.sendMessage(message: Any): Return {
    return this.sendMessage(message, Return::class)
}

suspend inline fun <reified Message : Any> WiFiDirectConnector.requestStream(
    type: String,
    noinline callback: (Message) -> Unit
): Closeable {
    return this.requestStream(type, callback, Message::class)
}