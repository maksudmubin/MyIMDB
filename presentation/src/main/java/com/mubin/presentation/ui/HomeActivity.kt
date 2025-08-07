package com.mubin.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mubin.presentation.ui.screen.MovieListScreen
import com.mubin.presentation.ui.screen.SplashScreen
import com.mubin.presentation.ui.theme.MyIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    val vm by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyIMDBTheme {
                val navController = rememberNavController()
                HomeNavGraph(
                    navController = navController,
                    vm = vm
                )
            }
        }
    }

    @Composable
    fun HomeNavGraph(navController: NavHostController = rememberNavController(), vm: HomeViewModel) {

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen(
                    viewModel = vm,
                    onNavigateToMovieList = {
                        navController.navigate("movies") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("movies") {
                MovieListScreen(
                    viewModel = vm,
                    onNavigateToWishlist = { navController.navigate("wishlist") },
                    onNavigateToDetails = { movie ->
                        navController.navigate("details/${movie.id}")
                    }
                )
            }
            composable("details/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                //MovieDetailsScreen(movieId = id)
            }
            composable("wishlist") {
                //WishlistScreen()
            }
        }
    }
}