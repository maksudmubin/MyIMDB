package com.mubin.presentation.ui.screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.presentation.R
import com.mubin.presentation.ui.HomeViewModel
import com.mubin.presentation.ui.util.NetworkHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash screen composable shown on app launch.
 *
 * Responsibilities:
 * - Checks if this is the first app launch.
 * - Shows a "No Internet" dialog if there is no connection on first launch.
 * - Triggers initial data sync via the [HomeViewModel].
 * - Navigates to the movie list screen once data sync is complete.
 * - Shows error dialogs with retry/exit options if sync fails.
 *
 * @param viewModel The [HomeViewModel] to interact with for data and state
 * @param onNavigateToMovieList Callback invoked when splash finishes and navigates to movie list
 * @param onFinish Callback invoked to exit or finish splash (e.g. on user exit)
 */
@Composable
fun SplashScreen(
    viewModel: HomeViewModel,
    onNavigateToMovieList: () -> Unit,
    onFinish: () -> Unit
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var showFirstLaunchDialog by remember { mutableStateOf(false) }

    // Check if first launch and internet connectivity
    LaunchedEffect(Unit) {
        MyImdbLogger.d("SplashScreen", "Checking if first launch")
        viewModel.checkIfFirstLaunch { isFirstLaunch ->
            MyImdbLogger.d("SplashScreen", "Is first launch: $isFirstLaunch")
            if (isFirstLaunch) {
                if (!NetworkHelper(context).hasInternetConnection()) {
                    MyImdbLogger.d("SplashScreen", "No internet on first launch, showing dialog")
                    showFirstLaunchDialog = true
                }
            }
        }
    }

    // Navigate to movie list after data sync completes
    LaunchedEffect(uiState.isDataSynced) {
        if (uiState.isDataSynced) {
            MyImdbLogger.d("SplashScreen", "Data synced, navigating to movie list")
            scope.launch {
                delay(1000) // pause for splash effect
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

            CircularProgressIndicator()
        }

        // Show dialog if no internet on first launch
        if (showFirstLaunchDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("No Internet") },
                text = { Text("Internet connection is required on first launch.") },
                confirmButton = {
                    TextButton(onClick = {
                        MyImdbLogger.d("SplashScreen", "Retry clicked in no internet dialog")
                        if (NetworkHelper(context).hasInternetConnection()) {
                            showFirstLaunchDialog = false
                            viewModel.syncInitialData()
                        }
                    }) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        MyImdbLogger.d("SplashScreen", "Exit clicked in no internet dialog")
                        showFirstLaunchDialog = false
                        onFinish()
                    }) {
                        Text("Exit")
                    }
                }
            )
        }

        // Show dialog on sync error with retry/exit options
        uiState.error?.let { errorMsg ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Sync Failed") },
                text = { Text(errorMsg) },
                confirmButton = {
                    TextButton(onClick = {
                        MyImdbLogger.d("SplashScreen", "Retry clicked in sync error dialog")
                        viewModel.clearError()
                        if (NetworkHelper(context).hasInternetConnection()) {
                            viewModel.syncInitialData()
                        } else {
                            showFirstLaunchDialog = true
                        }
                    }) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        MyImdbLogger.d("SplashScreen", "Exit clicked in sync error dialog")
                        viewModel.clearError()
                        onFinish()
                    }) {
                        Text("Exit")
                    }
                }
            )
        }
    }
}