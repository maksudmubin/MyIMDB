package com.mubin.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mubin.presentation.ui.screen.MovieDetailsScreen
import com.mubin.presentation.ui.screen.MovieListScreen
import com.mubin.presentation.ui.screen.SplashScreen
import com.mubin.presentation.ui.screen.WishlistScreen
import com.mubin.presentation.ui.theme.MyIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }

            LaunchedEffect(systemDarkTheme) {
                isDarkTheme = systemDarkTheme
            }

            MyIMDBTheme(
                darkTheme = isDarkTheme
            ) {
                val navController = rememberNavController()
                HomeNavGraph(
                    navController = navController,
                    vm = vm,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = {
                        isDarkTheme = !isDarkTheme
                    }
                )
            }
        }
    }

    @Composable
    fun HomeNavGraph(
        navController: NavHostController = rememberNavController(),
        vm: HomeViewModel,
        isDarkTheme: Boolean,
        onToggleTheme: () -> Unit
    ) {

        NavHost(navController = navController, startDestination = "splash") {
            composable(
                route = "splash",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                }
            ) {
                SplashScreen(
                    viewModel = vm,
                    onNavigateToMovieList = {
                        navController.navigate("movies") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onFinish = { finish() }
                )
            }
            composable(
                route = "movies",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                }
            ) {
                MovieListScreen(
                    viewModel = vm,
                    onNavigateToWishlist = { navController.navigate("wishlist") },
                    onNavigateToDetails = { movie ->
                        navController.navigate("details?id=${movie.id}&isFromWishlist=false")
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
            composable(
                route = "details?id={id}&isFromWishlist={isFromWishlist}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    },
                    navArgument("isFromWishlist") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                val isFromWishlist = backStackEntry.arguments?.getBoolean("isFromWishlist") ?: false
                Log.d("HomeActivity", "$id isFromWishlist: $isFromWishlist")
                MovieDetailsScreen(
                    id = id,
                    viewmodel = vm,
                    onNavigateToWishlist = {
                        if (isFromWishlist) {
                            navController.popBackStack()
                        } else {
                            navController.navigate("wishlist")
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "wishlist",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,  // Should match enter of next screen
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,  // Reverse for back navigation
                        animationSpec = tween(500)
                    )
                }
            ) {
                WishlistScreen(
                    viewmodel = vm,
                    onNavigateToDetails = { movieId ->
                        navController.navigate("details?id=$movieId&isFromWishlist=true")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}