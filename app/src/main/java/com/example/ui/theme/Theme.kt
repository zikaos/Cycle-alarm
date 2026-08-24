package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LavenderAccent,
    onPrimary = DeepVioletOnAccent,
    primaryContainer = DeepVioletContainer,
    onPrimaryContainer = LavenderAccent,
    secondary = LavenderAccent,
    onSecondary = DeepVioletOnAccent,
    secondaryContainer = SlatePillSelected,
    onSecondaryContainer = TextWhite,
    tertiary = LavenderAccent,
    onTertiary = DeepVioletOnAccent,
    tertiaryContainer = LavenderRecommendedBg,
    onTertiaryContainer = LavenderAccent,
    background = ImmersiveDarkBg,
    onBackground = TextLight,
    surface = ImmersiveDarkBg,
    onSurface = TextWhite,
    surfaceVariant = ImmersiveSurfaceVariant,
    onSurfaceVariant = TextLight,
    outline = ImmersiveCardBorder,
    outlineVariant = ImmersiveCardBorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightPrimary,
    onSecondary = LightOnPrimary,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    tertiary = LightPrimary,
    onTertiary = LightOnPrimary,
    tertiaryContainer = LightPrimaryContainer,
    onTertiaryContainer = LightOnPrimaryContainer,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    outlineVariant = Color(0xFFD4D4E0)
)

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
