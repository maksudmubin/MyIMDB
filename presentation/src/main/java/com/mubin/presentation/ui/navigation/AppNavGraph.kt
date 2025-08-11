package com.mubin.presentation.ui.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mubin.presentation.ui.screen.movie_details.MovieDetailsScreen
import com.mubin.presentation.ui.screen.movie_details.MovieDetailsViewModel
import com.mubin.presentation.ui.screen.movie_list.MovieListScreen
import com.mubin.presentation.ui.screen.movie_list.MovieListViewModel
import com.mubin.presentation.ui.screen.splash.SplashScreen
import com.mubin.presentation.ui.screen.splash.SplashViewModel
import com.mubin.presentation.ui.screen.wishlist.WishlistScreen
import com.mubin.presentation.ui.screen.wishlist.WishlistViewModel

/**
 * Main navigation graph for the MyIMDB app.
 *
 * This composable sets up the app's navigation using [NavHost], defining all
 * the available destinations and their associated transitions, ViewModels, and
 * navigation actions.
 *
 * Screens included in this navigation graph:
 * - Splash Screen (Start Destination)
 * - Movie List Screen
 * - Movie Details Screen
 * - Wishlist Screen
 *
 * @param navController [NavHostController] instance used to manage app navigation.
 * Defaults to a new instance created via [rememberNavController].
 * @param isDarkTheme Boolean flag indicating whether the app is currently in dark mode.
 * @param onToggleTheme Lambda that toggles between light and dark theme.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    // Set the start destination to Splash Screen
    NavHost(navController = navController, startDestination = "splash") {

        /**
         * Splash Screen destination.
         * - Loads initial data (sync from API to DB if needed).
         * - Navigates to the Movie List screen after completion.
         * - Finishes the activity if needed.
         */
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
            // Obtain ViewModel via Hilt
            val splashViewModel: SplashViewModel = hiltViewModel()

            // Show Splash UI
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateToMovieList = {
                    // Navigate to movies and clear Splash from back stack
                    navController.navigate("movies") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onFinish = { (navController.context as? Activity)?.finish() }
            )
        }

        /**
         * Movie List Screen destination.
         * - Displays the list/grid of movies.
         * - Allows navigation to Wishlist or Movie Details.
         * - Supports theme toggle.
         */
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
            val movieListViewModel: MovieListViewModel = hiltViewModel()

            MovieListScreen(
                viewModel = movieListViewModel,
                onNavigateToWishlist = { navController.navigate("wishlist") },
                onNavigateToDetails = { movie ->
                    navController.navigate("details?id=${movie.id}&isFromWishlist=false")
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        /**
         * Movie Details Screen destination.
         * - Displays full details of a selected movie.
         * - Handles navigation back or to Wishlist.
         *
         * @param id Movie ID to load details for.
         * @param isFromWishlist Boolean indicating if the screen was opened from the Wishlist.
         */
        composable(
            route = "details?id={id}&isFromWishlist={isFromWishlist}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("isFromWishlist") { type = NavType.BoolType; defaultValue = false }
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

            val movieDetailsViewModel: MovieDetailsViewModel = hiltViewModel()

            MovieDetailsScreen(
                movieId = id,
                viewModel = movieDetailsViewModel,
                onNavigateToWishlist = {
                    if (isFromWishlist) navController.popBackStack()
                    else navController.navigate("wishlist")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        /**
         * Wishlist Screen destination.
         * - Displays all movies marked as wishlist items.
         * - Allows navigation to Movie Details or back to the previous screen.
         */
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
            val wishlistViewModel: WishlistViewModel = hiltViewModel()

            WishlistScreen(
                viewModel = wishlistViewModel,
                onNavigateToDetails = { movieId ->
                    navController.navigate("details?id=$movieId&isFromWishlist=true")
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
