package com.marcomichaelis.groupify.communication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.marcomichaelis.groupify.spotify.SpotifyClient
import com.marcomichaelis.groupify.spotify.models.Track
import com.marcomichaelis.groupify.storage.TrackRoomDatabase
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RPCWiFiDirectServer
constructor(
    private val spotifyClient: SpotifyClient,
    internal val port: Int,
    private val appContext: Context
) : WiFiDirectServer {

    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var server: ApplicationEngine
    private val messageFlow =
        MutableSharedFlow<WebsocketMessage>(
            replay = 0,
            extraBufferCapacity = 1,
            BufferOverflow.DROP_OLDEST
        )
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            spotifyClient.streamPlaybackState {
                messageFlow.emit(WebsocketMessage("playbackState", json.encodeToString(it)))
            }
            spotifyClient.streamPlaylist {
                    messageFlow.emit(WebsocketMessage("playlist", json.encodeToString(it)))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun start() {
        server =
            embeddedServer(Netty, port = port) {
                    install(ContentNegotiation) { json(json) }
                    install(WebSockets) {
                        pingPeriodMillis = TimeUnit.SECONDS.toMillis(2)
                        timeoutMillis = TimeUnit.MINUTES.toMillis(1)
                    }
                    routing {
                        post("/rpc") {
                            val message = call.receive<RemoteCall>()
                            println(message)
                            println(spotifyClient)
                            try {
                                call.respond(
                                    when (message.methodName) {
                                        "search" -> spotifyClient.search(message.parameters[0]!!)
                                        "getDevices" -> spotifyClient.getDevices()
                                        "play" ->
                                            spotifyClient.play(
                                                message.parameters[0],
                                            )
                                        "pause" -> spotifyClient.pause()
                                        "setVolume" ->
                                            spotifyClient.setVolume(message.parameters[0]!!.toInt())
                                        "addTrack" -> {
                                            println(
                                                json.decodeFromString<Track>(
                                                    message.parameters[0]!!
                                                )
                                            )
                                            TrackRoomDatabase.getInstance(appContext)
                                                .trackDao()
                                                .insertTrack(
                                                    json.decodeFromString(message.parameters[0]!!)
                                                )
                                        }
                                        "streamPlaybackState" -> "Use /stream to stream"
                                        else -> "Unknown method name"
                                    }
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                throw e
                            }
                        }
                        webSocket("/stream") {
                            val subscribedTopics = mutableListOf<String>()

                            launch {
                                messageFlow.collect {
                                    if (it.topic in subscribedTopics) {
                                        println("sending ${json.encodeToString(it)}")
                                        send(json.encodeToString(it))
                                    }
                                }
                            }

                            for (frame in incoming) {
                                val message = frame as? Frame.Text ?: continue
                                val info =
                                    json.decodeFromString<SubscriptionInfo>(message.readText())
                                println("received $info")
                                subscribedTopics.add(info.topic)
                            }
                        }
                    }
                }
                .start(wait = false)
    }

    override fun stop() {
        server.stop(500)
        scope.cancel()
    }

    override fun publish(topic: String, message: String) {
        messageFlow.tryEmit(WebsocketMessage(topic, message))
    }
}
