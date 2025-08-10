package com.mubin.presentation.ui.navigation

import android.app.Activity
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

        composable("splash") {
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

        composable("movies") {
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
                navArgument("isFromWishlist") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
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

        composable("wishlist") {
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