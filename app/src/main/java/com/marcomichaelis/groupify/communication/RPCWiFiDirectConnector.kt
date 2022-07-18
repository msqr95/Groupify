package com.marcomichaelis.groupify.communication

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import java.io.EOFException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias WSCallback = suspend (String) -> Unit

class RPCWiFiDirectConnector constructor(private val host: String, private val port: Int) :
    WiFiDirectConnector {
    private val url: String
        get() = "http://$host:$port"
    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val callbacksByTopic = mutableMapOf<String, MutableList<WSCallback>>()
    private val sendFlow =
        MutableSharedFlow<String>(replay = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val client =
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(WebSockets) { pingInterval = TimeUnit.SECONDS.toMillis(2) }
        }

    init {
        scope.launch {
            client.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/stream") {
                launch {
                    sendFlow.collect {
                        println("client sending $it")
                        send(it)
                    }
                }

                try {
                    for (frame in incoming) {
                        val rawMessage = frame as? Frame.Text ?: continue
                        val (topic, message) =
                            json.decodeFromString<WebsocketMessage>(rawMessage.readText())
                        println("client received $topic: $message")
                        val callbacks = callbacksByTopic[topic]
                        callbacks?.forEach { it(message) }
                    }
                } catch (e: EOFException) {
                    // server stopped
                }
            }
        }
    }

    override suspend fun sendRemoteCall(
        remoteCall: RemoteCall,
    ): String {
        val response =
            client.post("$url/rpc") {
                contentType(ContentType.Application.Json)
                setBody(remoteCall)
            }

        if (response.status != HttpStatusCode.OK) {
            throw Exception(response.body<String>() + " " + response.status)
        }

        return response.bodyAsText()
    }

    override suspend fun requestStream(
        topic: String,
        callback: suspend (String) -> Unit,
    ) {
        if (topic !in callbacksByTopic) {
            println("subscribing to topic $topic")
            callbacksByTopic[topic] = mutableListOf()
            scope.launch { sendFlow.emit(json.encodeToString(SubscriptionInfo(topic))) }.join()
            println("finished emitting")
        }
        callbacksByTopic[topic]?.add(callback)
    }
}
