package com.marcomichaelis.groupify

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.marcomichaelis.groupify.components.Button

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Router() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController = navController, startDestination = "start") {
        composable("start", exitTransition = {
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
        }) {
            Column {
                Text(text = "Hier ist 1 nicer start")
                Button(onClick = { navController.navigate("bruder") }) {
                    Text(text = "Bruder muss los")
                }
            }
        }

        composable("bruder", enterTransition = {
            slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
        }) {
            Text(text = "Bruder bin da")
        }

    }
}
