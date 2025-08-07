package com.mubin.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mubin.presentation.ui.HomeViewModel
import com.mubin.presentation.ui.util.NetworkHelper

@Composable
fun SplashScreen(
    viewModel: HomeViewModel,
    onNavigateToMovieList: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showNoInternetDialog by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground

    // Handle navigation logic
    LaunchedEffect(true) {
        viewModel.checkIfFirstLaunch { isFirstLaunch ->
            if (isFirstLaunch) {
                if (NetworkHelper(context).hasInternetConnection()) {
                    onNavigateToMovieList()
                } else {
                    showNoInternetDialog = true
                }
            } else {
                onNavigateToMovieList()
            }
        }
    }

    // UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "App Logo",
                tint = contentColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MyIMDB",
                color = contentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showNoInternetDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(onClick = { showNoInternetDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("No Internet") },
                text = { Text("Internet connection is required on first launch.") }
            )
        }
    }
}