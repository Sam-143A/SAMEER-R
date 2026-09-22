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
    primary = KsrtcRedLight,
    onPrimary = Color.White,
    primaryContainer = KsrtcRedDark,
    onPrimaryContainer = KsrtcGold,
    secondary = KsrtcAmber,
    onSecondary = DarkBg,
    secondaryContainer = Color(0xFF3E2723),
    onSecondaryContainer = KsrtcGold,
    tertiary = RadarCyan,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = KsrtcRedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = KsrtcRedDark,
    secondary = KsrtcAmber,
    onSecondary = DarkBg,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = Color(0xFF00838F),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek futuristic dark transit HUD theme
    dynamicColor: Boolean = false, // Keep brand KSRTC crimson & amber fidelity
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
