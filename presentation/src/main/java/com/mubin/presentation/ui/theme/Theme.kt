package com.mubin.presentation.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsetsController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mubin.common.utils.logger.MyImdbLogger

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

/**
 * Composable function that sets up the theme for the MyIMDB app.
 *
 * Applies dark or light color schemes, adjusts the system status bar color and appearance,
 * and wraps the content in [MaterialTheme] with typography and colors.
 *
 * @param darkTheme If true, uses dark theme colors; otherwise uses light theme.
 *                  Defaults to system dark mode setting.
 * @param content The composable content to apply the theme to.
 */
@Composable
fun MyIMDBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        val window = (context as? Activity)?.window
        if (window == null) {
            MyImdbLogger.d("MyIMDBTheme", "No activity window found; skipping status bar setup.")
            return@SideEffect
        }

        // Set status bar background color to match theme background
        @Suppress("DEPRECATION")
        window.statusBarColor = colorScheme.background.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For API 30+, control light/dark status bar icons with WindowInsetsController
            window.insetsController?.setSystemBarsAppearance(
                if (!darkTheme) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            MyImdbLogger.d("MyIMDBTheme", "Set status bar appearance for API 30+ with darkTheme=$darkTheme")
        } else {
            // For older APIs, use WindowCompat to control status bar icon color
            @Suppress("DEPRECATION")
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
            MyImdbLogger.d("MyIMDBTheme", "Set status bar appearance for pre-API 30 with darkTheme=$darkTheme")
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}