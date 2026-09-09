package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Novexa Material 3 Color Schemes
 */
private val DarkColorScheme = darkColorScheme(
    primary = NovexaBlueLight,
    onPrimary = Color.White,
    primaryContainer = NovexaBlueDark,
    onPrimaryContainer = Color.White,
    secondary = NovexaBlue,
    onSecondary = Color.White,
    secondaryContainer = NovexaDarkSurface,
    onSecondaryContainer = Color.White,
    tertiary = NovexaAccentAmber,
    onTertiary = Color.White,
    background = NovexaBlack,
    onBackground = Color(0xFFF1F5F9),
    surface = NovexaDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = NovexaMutedBlack,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = NovexaSlateGray,
    outlineVariant = NovexaMutedBlack,
    error = NovexaAccentRose,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NovexaBlue,
    onPrimary = Color.White,
    primaryContainer = NovexaBlueContainer,
    onPrimaryContainer = NovexaOnBlueContainer,
    secondary = NovexaBlack,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = NovexaBlack,
    tertiary = NovexaAccentAmber,
    onTertiary = Color.White,
    background = NovexaBgLight,
    onBackground = NovexaTextPrimary,
    surface = NovexaSurfaceLight,
    onSurface = NovexaTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NovexaTextSecondary,
    outline = NovexaCardBorder,
    outlineVariant = Color(0xFFF1F5F9),
    error = NovexaAccentRose,
    onError = Color.White
)

/**
 * Novexa Theme implementation conforming to Material Design 3.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Novexa signature blue + black brand aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = NovexaShapes,
        content = content
    )
}

@Composable
fun NovexaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MyApplicationTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        content = content
    )
}
