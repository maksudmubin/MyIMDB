package com.mubin.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.mubin.presentation.ui.navigation.AppNavGraph
import com.mubin.presentation.ui.theme.MyIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point activity for the MyIMDB application.
 *
 * This activity:
 * - Initializes the Compose content for the app.
 * - Detects the system's dark theme preference and applies it.
 * - Hosts the [AppNavGraph] which manages navigation between screens.
 *
 * Annotated with [AndroidEntryPoint] to support Hilt dependency injection.
 */
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    /**
     * Called when the activity is created.
     *
     * This method sets up:
     * - The app theme based on system settings.
     * - A toggle for switching between light and dark mode.
     * - The navigation graph for the entire app.
     *
     * @param savedInstanceState The saved state bundle, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Check the system's dark theme setting
            val systemDarkTheme = isSystemInDarkTheme()

            // App-wide theme state, remembered across configuration changes
            var isDarkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }

            // Update the theme state whenever the system theme changes
            LaunchedEffect(systemDarkTheme) {
                isDarkTheme = systemDarkTheme
            }

            // Apply the custom app theme
            MyIMDBTheme(darkTheme = isDarkTheme) {

                // Create a NavController to handle app navigation
                val navController = rememberNavController()

                // Set up the app's navigation graph
                AppNavGraph(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme } // Toggle theme on demand
                )
            }
        }
    }
}

