package com.marcomichaelis.groupify

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.marcomichaelis.groupify.pages.SpotifyLoginPage
import com.marcomichaelis.groupify.pages.StartPage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Router() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "start") {
        composable(
            "start",
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            }
        ) { StartPage(navController) }

        composable(
            "spotify-login",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            }
        ) { SpotifyLoginPage() }
    }
}
