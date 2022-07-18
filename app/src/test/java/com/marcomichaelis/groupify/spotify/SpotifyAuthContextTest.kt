package com.marcomichaelis.groupify.spotify

import com.spotify.sdk.android.auth.AuthorizationResponse
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class SpotifyAuthContextTest {

    private val spotifyAuthContext = spyk<SpotifyAuthContext>()
    private val jsonInstance = Json { ignoreUnknownKeys = true }

    @Test
    fun `should request auth token on successful auth`() {
        coEvery { spotifyAuthContext.requestAuthTokens(any()) } returns Unit
        val authResponse =
            mockk<AuthorizationResponse> {
                every { type } returns AuthorizationResponse.Type.CODE
                every { code } returns "auth-code"
            }

        spotifyAuthContext.handleAuthorizationResponse(authResponse)

        coVerify { spotifyAuthContext.requestAuthTokens("auth-code") }
    }

    @Test
    fun `should not request auth tokens on unsuccessful auth`() {
        coEvery { spotifyAuthContext.requestAuthTokens(any()) } returns Unit
        val authResponse =
            mockk<AuthorizationResponse> {
                every { type } returns AuthorizationResponse.Type.ERROR
                every { error } returns "Error message"
            }

        spotifyAuthContext.handleAuthorizationResponse(authResponse)

        coVerify(exactly = 0) { spotifyAuthContext.requestAuthTokens(any()) }
    }

    @Test
    fun `should provide auth tokens on successful request`(): Unit = runBlocking {
        val tokens = AuthTokens("accessToken", 3000, "refreshToken")
        val mockEngine = MockEngine {
            respond(
                content = jsonInstance.encodeToString(tokens),
                headers = headersOf(HttpHeaders.ContentType to listOf("application/json"))
            )
        }
        every { spotifyAuthContext.httpClient } returns
            HttpClient(mockEngine) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }

        val authTokensFlow = mockk<MutableSharedFlow<AuthTokens>>(relaxed = true)
        every { spotifyAuthContext.authTokens } returns authTokensFlow

        spotifyAuthContext.requestAuthTokens("auth-code")

        coVerify { authTokensFlow.emit(tokens) }
    }

    @Test
    fun `should not have any auth tokens on unsuccessful request`(): Unit = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        every { spotifyAuthContext.httpClient } returns
                HttpClient(mockEngine) {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                }

        val authTokensFlow = mockk<MutableSharedFlow<AuthTokens>>(relaxed = true)
        every { spotifyAuthContext.authTokens } returns authTokensFlow

        spotifyAuthContext.requestAuthTokens("auth-code")

        coVerify(exactly = 0) { authTokensFlow.emit(any()) }
    }
}
