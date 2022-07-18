package com.marcomichaelis.groupify.spotify

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.compositionLocalOf
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

const val ClientID = "c1bf7df523794dfcb04ad3d9fccf3ef5"
const val ClientSecret = "60b6c2de1bb74e36935455c8083990b2"
private const val RedirectUri = "groupify://callback"
private val scopes =
    arrayOf(
        "streaming",
        "user-read-playback-state",
        "user-read-currently-playing",
        "user-modify-playback-state"
    )

val LocalSpotifyAuthContext = compositionLocalOf { SpotifyAuthContext() }

class SpotifyAuthContext {
    private val tag = "SpotifyContext"
    internal val httpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(Auth) {
                basic {
                    sendWithoutRequest { true }
                    credentials { BasicAuthCredentials(ClientID, ClientSecret) }
                }
            }
        }
    val requestCode = 1337
    val authRequest: AuthorizationRequest
        get() =
            AuthorizationRequest.Builder(ClientID, AuthorizationResponse.Type.CODE, RedirectUri)
                .setScopes(scopes)
                .build()
    val authTokens = MutableSharedFlow<AuthTokens>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun handleAuthorizationResponse(response: AuthorizationResponse) = runBlocking {
        when (response.type) {
            AuthorizationResponse.Type.CODE -> requestAuthTokens(response.code)
            AuthorizationResponse.Type.ERROR ->
                Log.e(tag, "Authorization failed", Exception(response.error))
            AuthorizationResponse.Type.EMPTY ->
                Log.w(tag, "Login flow probably triggered the browser authentication")
            else -> {
                Log.e(tag, "Authorization failed ${response.type}")
            }
        }
    }

    internal suspend fun requestAuthTokens(authCode: String) {
        val response =
            httpClient.submitForm(
                url = "https://accounts.spotify.com/api/token",
                formParameters =
                    Parameters.build {
                        append("code", authCode)
                        append("redirect_uri", RedirectUri)
                        append("grant_type", "authorization_code")
                    }
            ) { method = HttpMethod.Post }
        if (response.status != HttpStatusCode.OK) {
            Log.e(tag, "Request failed ${response.body<String>()}")
            return
        }
        authTokens.emit(response.body())
    }
}
