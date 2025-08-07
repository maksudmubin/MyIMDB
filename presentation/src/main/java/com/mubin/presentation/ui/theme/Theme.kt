package com.mubin.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF37474F),           // Cool grey - buttons, icons, top bar
    onPrimary = Color.White,

    primaryContainer = Color(0xFFE0E0E0),   // TextFields, cards
    onPrimaryContainer = Color(0xFF1A1A1A),

    background = Color(0xFFF5F5F5),         // Overall background tone
    onBackground = Color(0xFF212121),

    surface = Color(0xFFFFFFFF),            // Elevated surfaces (app bar, dialog)
    onSurface = Color(0xFF212121),

    secondary = Color(0xFF607D8B),          // Muted accent (e.g., badges, filters)
    onSecondary = Color.White
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFE0E0E0),            // Light grey for top bar, buttons
    onPrimary = Color(0xFF121212),

    primaryContainer = Color(0xFF2A2A2A),   // Elevated containers
    onPrimaryContainer = Color(0xFFECECEC),

    background = Color(0xFF121212),         // True dark background
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),            // App bar, cards, dialog
    onSurface = Color(0xFFCCCCCC),

    secondary = Color(0xFF424242),          // Subtle accent
    onSecondary = Color(0xFFECECEC)
)

@Composable
fun MyIMDBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}