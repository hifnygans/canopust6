package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    ORIGINAL("Classic"),
    VIBRANT_GREEN("Deep Forest"),
    DYNAMIC_RED("Ruby Night"),
    SLEEK_DARK("Midnight Shadow"),
    OCEAN_BLUE("Azure Tide"),
    NEON_FUTURISTIC("Cyber Core"),
    MODERN_WHITE_BLUE("Frost Blue"),
    NIGHT_GLOW("Emerald Pulse"),
    RETRO_WHITE_GREEN("Vintage Lime"),
    DARK_LIME("Onyx Blade"),
    HYPER_VOLT("Thunder Volt"),
    CRIMSON_AZURE("Split Core"),
    SUNSET_ORANGE("Solar Flare"),
    PEARL_WHITE("Lunar Surface"),
    HACKER_VOID("Terminal Void")
}

fun getColorScheme(theme: AppTheme): ColorScheme {
    return when (theme) {
        AppTheme.ORIGINAL -> darkColorScheme(
            primary = Color(0xFFE91E63),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFC2185B),
            onPrimaryContainer = Color.White,
            secondary = Color(0xFF03A9F4),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
        AppTheme.VIBRANT_GREEN -> darkColorScheme( // Inspired by Spotify
            primary = Color(0xFF1DB954),
            onPrimary = Color.Black,
            background = Color(0xFF191414),
            surface = Color(0xFF212121),
            secondary = Color(0xFF1DB954)
        )
        AppTheme.DYNAMIC_RED -> darkColorScheme( // Inspired by Netflix
            primary = Color(0xFFE50914),
            onPrimary = Color.White,
            background = Color.Black,
            surface = Color(0xFF141414),
            secondary = Color(0xFFE50914)
        )
        AppTheme.SLEEK_DARK -> darkColorScheme( // Inspired by PS3
            primary = Color(0xFFB71C1C),
            onPrimary = Color.White,
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            secondary = Color(0xFF424242)
        )
        AppTheme.OCEAN_BLUE -> darkColorScheme( // Inspired by PS4
            primary = Color(0xFF003791),
            onPrimary = Color.White,
            background = Color(0xFF001F4D),
            surface = Color(0xFF002D6E),
            secondary = Color(0xFF00ABFF)
        )
        AppTheme.PEARL_WHITE -> lightColorScheme( // Inspired by PS5/Wii
            primary = Color(0xFF006FCD),
            onPrimary = Color.White,
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            secondary = Color(0xFF000000)
        )
        AppTheme.DARK_LIME -> darkColorScheme( // Inspired by Xbox
            primary = Color(0xFF107C10),
            onPrimary = Color.White,
            background = Color(0xFF0E0E0E),
            surface = Color(0xFF1A1A1A),
            secondary = Color(0xFF2CA02C)
        )
        AppTheme.RETRO_WHITE_GREEN -> lightColorScheme( // Inspired by Xbox 360
            primary = Color(0xFF107C10),
            onPrimary = Color.White,
            background = Color.White,
            surface = Color(0xFFF0F0F0),
            secondary = Color(0xFF808080)
        )
        AppTheme.NIGHT_GLOW -> darkColorScheme( // Inspired by Xbox X/S
            primary = Color(0xFF107C10),
            onPrimary = Color.White,
            background = Color.Black,
            surface = Color(0xFF050505),
            secondary = Color(0xFF107C10)
        )
        AppTheme.HACKER_VOID -> darkColorScheme( // Inspired by Hacker
            primary = Color(0xFF00FF41),
            onPrimary = Color.Black,
            background = Color.Black,
            surface = Color(0xFF0D0D0D),
            secondary = Color(0xFF00FF41)
        )
        AppTheme.MODERN_WHITE_BLUE -> lightColorScheme( // Inspired by Wii U
            primary = Color(0xFF009ACD),
            onPrimary = Color.White,
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFE1F5FE),
            secondary = Color(0xFF0277BD)
        )
        AppTheme.CRIMSON_AZURE -> darkColorScheme( // Inspired by Switch
            primary = Color(0xFFFF4B4B),
            onPrimary = Color.White,
            background = Color(0xFF1A1A1A),
            surface = Color(0xFF2D2D2D),
            secondary = Color(0xFF32C5FF)
        )
        AppTheme.SUNSET_ORANGE -> darkColorScheme( // Inspired by Amazon
            primary = Color(0xFFFF9900),
            onPrimary = Color.Black,
            background = Color(0xFF232F3E),
            surface = Color(0xFF37475A),
            secondary = Color(0xFF00A8E1)
        )
        AppTheme.HYPER_VOLT -> darkColorScheme( // Inspired by Switch 2 (Theoretical/Alternative)
            primary = Color(0xFFC0FF00),
            onPrimary = Color.Black,
            background = Color(0xFF111111),
            surface = Color(0xFF222222),
            secondary = Color(0xFFFF007F)
        )
        AppTheme.NEON_FUTURISTIC -> darkColorScheme(
            primary = Color(0xFFBB86FC),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            surface = Color(0xFF242424),
            secondary = Color(0xFF03DAC6)
        )
    }
}
