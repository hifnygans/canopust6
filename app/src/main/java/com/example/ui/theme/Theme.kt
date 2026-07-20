package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MD3PrimaryContainer,
    onPrimary = MD3OnPrimaryContainer,
    primaryContainer = MD3Primary,
    onPrimaryContainer = MD3OnPrimary,
    secondary = MD3Secondary,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    outline = MD3Outline,
    error = MD3Error
)

private val LightColorScheme = lightColorScheme(
    primary = MD3Primary,
    onPrimary = MD3OnPrimary,
    primaryContainer = MD3PrimaryContainer,
    onPrimaryContainer = MD3OnPrimaryContainer,
    secondary = MD3Secondary,
    background = MD3Background,
    surface = MD3Surface,
    outline = MD3Outline,
    error = MD3Error
)

@Composable
fun RetroAchievementsTheme(
    themeIndex: Int = 0,
    content: @Composable () -> Unit,
) {
    val theme = AppTheme.values().getOrElse(themeIndex) { AppTheme.ORIGINAL }
    val colorScheme = getColorScheme(theme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
