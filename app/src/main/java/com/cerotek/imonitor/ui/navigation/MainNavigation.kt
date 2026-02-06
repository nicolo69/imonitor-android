package com.cerotek.imonitor.ui.navigation

// Navigazione principale dell'app con tutte le schermate
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.cerotek.imonitor.ui.screens.home.HomeScreen
import com.cerotek.imonitor.ui.screens.settings.SettingsScreen
import com.cerotek.imonitor.ui.screens.info.InfoAziendaScreen
import com.cerotek.imonitor.ui.screens.updates.UpdatesScreen
import com.cerotek.imonitor.ui.screens.smartwatch.SmartwatchScreen
import com.cerotek.imonitor.ui.screens.parametri.ParametriScreen
import com.cerotek.imonitor.ui.screens.storico.StoricoScreen
import com.cerotek.imonitor.ui.screens.thresholds.ThresholdsScreen
import com.cerotek.imonitor.ui.screens.parameterdetail.ParameterDetailScreen
import androidx.compose.animation.core.tween

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            HomeScreen(navController = navController)
        }
        
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            SettingsScreen(navController = navController)
        }
        
        composable(
            route = Screen.InfoAzienda.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) +
                        fadeOut(tween(300))
            }
        ) {
            InfoAziendaScreen(navController = navController)
        }
        
        composable(
            route = Screen.Updates.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            UpdatesScreen(navController = navController)
        }
        
        composable(
            route = Screen.Smartwatch.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            SmartwatchScreen(navController = navController)
        }
        
        composable(
            route = Screen.Parametri.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            ParametriScreen(navController = navController)
        }
        
        composable(
            route = Screen.Storico.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            StoricoScreen(navController = navController)
        }
        
        composable(
            route = Screen.Thresholds.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        fadeOut(tween(300))
            }
        ) {
            ThresholdsScreen(navController = navController)
        }
        
        composable(
            route = Screen.ParameterDetail.route,
            arguments = listOf(navArgument("parameterType") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        scaleIn(initialScale = 0.95f)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        scaleOut(targetScale = 0.95f)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) +
                        scaleIn(initialScale = 0.95f)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) +
                        scaleOut(targetScale = 0.95f)
            }
        ) { backStackEntry ->
            val parameterType = backStackEntry.arguments?.getString("parameterType") ?: ""
            ParameterDetailScreen(
                navController = navController,
                parameterType = parameterType
            )
        }
    }
}
