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

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {

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
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateToMovieList = {
                    navController.navigate("movies") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onFinish = { (navController.context as? Activity)?.finish() }
            )
        }

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