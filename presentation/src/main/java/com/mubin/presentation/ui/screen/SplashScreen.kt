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
import com.mubin.presentation.R
import com.mubin.presentation.ui.HomeViewModel
import com.mubin.presentation.ui.util.NetworkHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    LaunchedEffect(Unit) {
        viewModel.checkIfFirstLaunch { isFirstLaunch ->
            if (isFirstLaunch) {
                if (!NetworkHelper(context).hasInternetConnection()) {
                    showFirstLaunchDialog = true
                }
            }
        }
    }

    LaunchedEffect(uiState.isDataSynced) {
        if (uiState.isDataSynced) {
            scope.launch {
                delay(1000) // pause for dramatic effect of splash screen
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
                fontSize = with(density) {24.sp / fontScale},
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator()
        }

        if (showFirstLaunchDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("No Internet") },
                text = { Text("Internet connection is required on first launch.") },
                confirmButton = {
                    TextButton(onClick = {
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
                        showFirstLaunchDialog = false
                        onFinish()
                    }) {
                        Text("Exit")
                    }
                }
            )
        }

        uiState.error?.let { errorMsg ->
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Sync Failed") },
                text = { Text(errorMsg) },
                confirmButton = {
                    TextButton(onClick = {
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