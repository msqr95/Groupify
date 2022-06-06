package com.marcomichaelis.groupify.spotify

import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val RedirectUri = "groupify://callback"
private const val ClientID = "c1bf7df523794dfcb04ad3d9fccf3ef5"
private const val ClientSecret = "60b6c2de1bb74e36935455c8083990b2"
private val scopes =
    arrayOf(
        "streaming",
        "user-read-playback-state",
        "user-read-currently-playing",
        "user-modify-playback-state"
    )

val LocalSpotifyContext = compositionLocalOf { SpotifyContext() }

class SpotifyContext {
    private val tag = "SpotifyContext"
    private val httpClient = HttpClient(Android) {
        install(Auth) {
            basic {
                sendWithoutRequest { true }
                credentials { BasicAuthCredentials(ClientID, ClientSecret) }
            }
        }
    }
    private val callbacks = mutableListOf<() -> Unit>()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val json = Json { ignoreUnknownKeys = true }
    val authRequest: AuthorizationRequest
        get() =
            AuthorizationRequest.Builder(ClientID, AuthorizationResponse.Type.CODE, RedirectUri)
                .setScopes(scopes)
                .build()
    var authTokens: AuthTokens? = null
        private set

    fun addCallback(callback: () -> Unit) = callbacks.add(callback)

    private fun notifyCallbacks() = callbacks.asReversed().forEach { it() }

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

    private suspend fun requestAuthTokens(authCode: String) {
        val response =
            httpClient.submitForm(url = "https://accounts.spotify.com/api/token", formParameters = Parameters.build {
                append("code", authCode)
                append("redirect_uri", RedirectUri)
                append("grant_type", "authorization_code")
            }) { method = HttpMethod.Post }
        if (response.status != HttpStatusCode.OK) {
            Log.e(tag, "Request failed ${response.body<String>()}")
            return
        }
        authTokens = json.decodeFromString<AuthTokens>(response.body())

        notifyCallbacks()
        executor.schedule(
            {
                // ToDo Refresh Tokensksksksk
                println("refreshing token")
            },
            authTokens!!.expiresIn,
            TimeUnit.SECONDS
        )
    }
}
