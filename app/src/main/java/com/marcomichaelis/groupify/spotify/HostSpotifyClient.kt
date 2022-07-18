package com.marcomichaelis.groupify.spotify

import com.marcomichaelis.groupify.p2p.Type
import com.marcomichaelis.groupify.spotify.models.Device
import com.marcomichaelis.groupify.spotify.models.PlaybackState
import com.marcomichaelis.groupify.spotify.models.Track
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

private const val SpotifyBaseUrl = "https://api.spotify.com/v1"

@Serializable
data class PlayBody(
    @SerialName("uris") val trackUris: List<String>?,
    @SerialName("position_ms") val position: Int = 0
)

class HostSpotifyClient constructor(private var authTokens: AuthTokens) : SpotifyClient {

    override val type: Type
        get() = Type.HOST
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
    private val playbackStateCallbacks = mutableListOf<suspend (PlaybackState?) -> Unit>()
    private val playlistCallbacks = mutableListOf<suspend (List<Track>) -> Unit>()
    private val httpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(Auth) {
                bearer {
                    loadTokens { BearerTokens(authTokens.accessToken, authTokens.refreshToken) }
                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                        val newAccessToken = refreshAccessToken(this.client, refreshToken)
                        BearerTokens(newAccessToken, refreshToken)
                    }
                }
            }
        }

    init {
        pollPlaybackState()
    }

    override suspend fun search(keyword: String): List<Track> {
        if (keyword.isEmpty()) {
            return listOf()
        }

        val response =
            httpClient.get("$SpotifyBaseUrl/search") {
                url {
                    parameter("q", keyword)
                    parameter("type", "track")
                }
            }
        if (!response.status.isSuccess()) {
            throw Exception("${response.status} ${response.bodyAsText()}")
        }
        return Track.listFromJson(json.parseToJsonElement(response.bodyAsText()))
    }

    override suspend fun getDevices(): List<Device> {
        val response = httpClient.get("$SpotifyBaseUrl/me/player/devices")
        val body = response.body<JsonElement>()
        return json.decodeFromJsonElement(body.jsonObject["devices"] ?: return emptyList())
    }

    override suspend fun play(trackUri: String?): Boolean {
        val activeDevice =
            getDevices().maxByOrNull { it.isActive or (it.type == "computer") } ?: return false
        val response =
            httpClient.put("$SpotifyBaseUrl/me/player/play") {
                url { parameter("device_id", activeDevice.id) }
                contentType(ContentType.Application.Json)
                setBody(PlayBody(trackUris = if (trackUri == null) null else listOf(trackUri)))
            }
        println(response.bodyAsText())
        return response.status.isSuccess()
    }

    override suspend fun pause(): Boolean {
        val activeDevice =
            getDevices().maxByOrNull { it.isActive or (it.type == "computer") } ?: return false
        val response =
            httpClient.put("$SpotifyBaseUrl/me/player/pause") {
                url { parameter("device_id", activeDevice.id) }
            }
        return response.status.isSuccess()
    }

    override suspend fun setVolume(percent: Int): Boolean {
        val activeDevice =
            getDevices().maxByOrNull { it.isActive or (it.type == "computer") } ?: return false
        val response =
            httpClient.put("$SpotifyBaseUrl/me/player/volume") {
                url {
                    parameter("device_id", activeDevice.id)
                    parameter("volume_percent", percent)
                }
            }
        return response.status.isSuccess()
    }

    override suspend fun streamPlaybackState(callback: suspend (PlaybackState?) -> Unit) {
        playbackStateCallbacks.add(callback)
    }

    override suspend fun streamPlaylist(callback: suspend (List<Track>) -> Unit) {
        playlistCallbacks.add(callback)
    }

    override suspend fun addTrack(track: Track) {
        // noop
    }

    suspend fun pushPlaylistChange(playlist: List<Track>) {
        coroutineScope { playlistCallbacks.map { async { it(playlist) } }.awaitAll() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun pollPlaybackState() =
        CoroutineScope(newSingleThreadContext("polling")).launch {
            while (true) {
                val playbackState = getPlaybackState()
                withContext(Dispatchers.Main) {
                    playbackStateCallbacks.map { async { it(playbackState) } }.awaitAll()
                }
                delay(2000)
            }
        }

    private suspend fun getPlaybackState(): PlaybackState? {
        val response = httpClient.get("${SpotifyBaseUrl}/me/player")
        if ((response.status == HttpStatusCode.NoContent) or !response.status.isSuccess()) {
            return null
        }
        return response.body()
    }

    private suspend fun refreshAccessToken(client: HttpClient, refreshToken: String): String {
        val response =
            client.submitForm(
                url = "https://accounts.spotify.com/api/token",
                formParameters =
                    Parameters.build {
                        append("refresh_token", refreshToken)
                        append("grant_type", "refresh_token")
                    }
            ) { method = HttpMethod.Post }
        val body = response.body<JsonElement>()
        println(body)
        return body.jsonObject["access_token"]?.jsonPrimitive?.content
            ?: throw Exception("No access token in response")
    }
}
