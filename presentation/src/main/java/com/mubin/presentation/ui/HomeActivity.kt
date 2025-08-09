package com.mubin.presentation.ui

import android.os.Bundle
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
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.presentation.ui.screen.MovieDetailsScreen
import com.mubin.presentation.ui.screen.MovieListScreen
import com.mubin.presentation.ui.screen.SplashScreen
import com.mubin.presentation.ui.screen.WishlistScreen
import com.mubin.presentation.ui.theme.MyIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    // ViewModel instance scoped to this Activity
    val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Detect system dark theme preference
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

    /**
     * Navigation graph composable defining all app screens and transitions.
     *
     * @param navController Navigation controller used to navigate between screens
     * @param vm Shared HomeViewModel to provide UI state and handle events
     * @param isDarkTheme Current dark mode state
     * @param onToggleTheme Callback to switch theme mode
     */
    @Composable
    fun HomeNavGraph(
        navController: NavHostController = rememberNavController(),
        vm: HomeViewModel,
        isDarkTheme: Boolean,
        onToggleTheme: () -> Unit
    ) {
        NavHost(navController = navController, startDestination = "splash") {
            // Splash screen route with enter/exit animations
            composable(
                route = "splash",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {
                SplashScreen(
                    viewModel = vm,
                    onNavigateToMovieList = {
                        MyImdbLogger.d("HomeActivity", "Navigating from Splash to Movies list")
                        navController.navigate("movies") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onFinish = {
                        MyImdbLogger.d("HomeActivity", "Splash finished, exiting app")
                        finish()
                    }
                )
            }

            // Movie list screen route
            composable(
                route = "movies",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {
                MovieListScreen(
                    viewModel = vm,
                    onNavigateToWishlist = {
                        MyImdbLogger.d("HomeActivity", "Navigating to Wishlist screen from MovieList")
                        navController.navigate("wishlist")
                    },
                    onNavigateToDetails = { movie ->
                        MyImdbLogger.d("HomeActivity", "Navigating to Details screen for movie id=${movie.id}")
                        navController.navigate("details?id=${movie.id}&isFromWishlist=false")
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }

            // Movie details screen route with arguments and transitions
            composable(
                route = "details?id={id}&isFromWishlist={isFromWishlist}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("isFromWishlist") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                val isFromWishlist = backStackEntry.arguments?.getBoolean("isFromWishlist") ?: false
                MyImdbLogger.d("HomeActivity", "Details screen loaded with id=$id, isFromWishlist=$isFromWishlist")
                MovieDetailsScreen(
                    id = id,
                    viewmodel = vm,
                    onNavigateToWishlist = {
                        if (isFromWishlist) {
                            MyImdbLogger.d("HomeActivity", "Returning to Wishlist from Details screen")
                            navController.popBackStack()
                        } else {
                            MyImdbLogger.d("HomeActivity", "Navigating to Wishlist from Details screen")
                            navController.navigate("wishlist")
                        }
                    },
                    onBackClick = {
                        MyImdbLogger.d("HomeActivity", "Back pressed on Details screen")
                        navController.popBackStack()
                    }
                )
            }

            // Wishlist screen route
            composable(
                route = "wishlist",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {
                WishlistScreen(
                    viewmodel = vm,
                    onNavigateToDetails = { movieId ->
                        MyImdbLogger.d("HomeActivity", "Navigating to Details screen for movieId=$movieId from Wishlist")
                        navController.navigate("details?id=$movieId&isFromWishlist=true")
                    },
                    onBackClick = {
                        MyImdbLogger.d("HomeActivity", "Back pressed on Wishlist screen")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}