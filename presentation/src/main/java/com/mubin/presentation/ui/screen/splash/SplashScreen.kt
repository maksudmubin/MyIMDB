package com.mubin.presentation.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mubin.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash screen composable shown on app launch.
 *
 * Responsibilities:
 * - Checks if this is the first app launch.
 * - Shows a "No Internet" dialog if there is no connection on first launch.
 * - Triggers initial data sync via the [SplashViewModel].
 * - Navigates to the movie list screen once data sync is complete.
 * - Shows error dialogs with retry/exit options if sync fails.
 *
 * @param viewModel The [SplashViewModel] to interact with for data and state
 * @param onNavigateToMovieList Callback invoked when splash finishes and navigates to movie list
 * @param onFinish Callback invoked to exit or finish splash (e.g. on user exit)
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToMovieList: () -> Unit,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Kick off first launch check
    LaunchedEffect(Unit) {
        viewModel.handleIntent(SplashUiIntent.CheckFirstLaunch)
    }

    // Navigate when data is synced
    LaunchedEffect(uiState.isDataSynced) {
        if (uiState.isDataSynced) {
            scope.launch {
                delay(1000) // splash delay
                onNavigateToMovieList()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.movie_app_icon),
                contentDescription = "App Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My IMDB",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = with(density) { 24.sp / fontScale },
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
        }

        // No internet dialog
        if (uiState.showNoInternetDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("No Internet") },
                text = { Text("Internet connection is required on first launch.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.handleIntent(SplashUiIntent.RetrySync) }) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onFinish() }) {
                        Text("Exit")
                    }
                }
            )
        }

        // Sync error dialog
        uiState.error?.let { errorMsg ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Sync Failed") },
                text = { Text(errorMsg) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearError()
                        viewModel.handleIntent(SplashUiIntent.RetrySync)
                    }) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onFinish() }) {
                        Text("Exit")
                    }
                }
            )
        }
    }
}