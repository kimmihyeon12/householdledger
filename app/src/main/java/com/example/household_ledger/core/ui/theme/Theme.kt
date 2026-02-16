package com.example.household_ledger.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = Color.White,
    primaryContainer = Navy100,
    onPrimaryContainer = Navy900,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Navy900,
    tertiary = CategoryTransport,
    onTertiary = Color.White,
    tertiaryContainer = IncomeSoft,
    onTertiaryContainer = Navy900,
    background = SurfaceLight,
    onBackground = Navy900,
    surface = CardLight,
    onSurface = Navy900,
    surfaceVariant = SurfaceDim,
    onSurfaceVariant = Navy600,
    outline = Navy200,
    outlineVariant = Navy100,
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = ExpenseSoft,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = Navy100,
    secondary = GoldLight,
    onSecondary = Navy900,
    secondaryContainer = Navy700,
    onSecondaryContainer = GoldLight,
    tertiary = CategoryTransport,
    onTertiary = Color.White,
    tertiaryContainer = Navy700,
    onTertiaryContainer = IncomeSoft,
    background = Navy900,
    onBackground = Navy100,
    surface = Navy800,
    onSurface = Navy100,
    surfaceVariant = Navy700,
    onSurfaceVariant = Navy400,
    outline = Navy600,
    outlineVariant = Navy700,
    error = Color(0xFFFCA5A5),
    onError = Navy900,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFCA5A5)
)

@Composable
fun AutoLedgerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalThemeMode provides themeMode,
        LocalOnThemeModeChange provides onThemeModeChange,
        LocalIsDark provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
