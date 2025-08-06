package com.mubin.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.mubin.network.util.NetworkResult
import com.mubin.presentation.ui.theme.MyIMDBTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    val vm by viewModels<HomeViewmodel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyIMDBTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    scope.launch {
                        val result = vm.initializeMovies()
                        when (result) {
                            is NetworkResult.Success -> {
                                // Handle success
                            }
                            is NetworkResult.Error -> {
                                // Handle error
                            }
                            NetworkResult.Loading -> {
                                // Handle loading
                            }
                        }
                    }
                }

            }
        }
    }
}