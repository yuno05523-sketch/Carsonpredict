package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldNeon,
    onPrimary = Color(0xFF003818),
    primaryContainer = Color(0xFF005327),
    onPrimaryContainer = EmeraldLight,
    secondary = GoldTrophy,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = Color(0xFF614100),
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = AccentCyan,
    onTertiary = Color(0xFF00363D),
    background = PitchDark,
    onBackground = TextPrimary,
    surface = PitchSurface,
    onSurface = TextPrimary,
    surfaceVariant = PitchSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = PitchBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For FasoPredict AI, a dedicated sporty dark pitch scheme provides the best contrast
    // and visibility for odds, probabilities and tactical telemetry.
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
