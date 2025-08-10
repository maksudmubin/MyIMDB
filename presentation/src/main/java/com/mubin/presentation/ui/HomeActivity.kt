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

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }

            LaunchedEffect(systemDarkTheme) {
                isDarkTheme = systemDarkTheme
            }

            MyIMDBTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

