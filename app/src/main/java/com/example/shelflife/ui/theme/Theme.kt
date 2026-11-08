package com.example.shelflife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Navy is primary (top bar, buttons, focused fields), mauve and dusty pink are the
// contrast (+ button, selected chips, selected tab), white is the background.
private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = White,
    primaryContainer = SkyTint,
    onPrimaryContainer = Navy,
    secondary = Mauve,
    onSecondary = Midnight,
    secondaryContainer = DustyPink,
    onSecondaryContainer = Navy,
    tertiary = SteelBlue,
    onTertiary = White,
    tertiaryContainer = SkyTint,
    onTertiaryContainer = Midnight,
    background = White,
    onBackground = Midnight,
    surface = White,
    onSurface = Midnight,
    surfaceVariant = SkyTint,
    onSurfaceVariant = SteelBlue,
    surfaceContainerLowest = White,
    surfaceContainerLow = OffWhite,
    surfaceContainer = OffWhite,
    surfaceContainerHigh = SkyTint,
    surfaceContainerHighest = SkyTint,
    outline = SteelBlue,
    outlineVariant = SkyTint
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Midnight,
    primaryContainer = SteelBlue,
    onPrimaryContainer = White,
    secondary = Mauve,
    onSecondary = Midnight,
    secondaryContainer = Mauve,
    onSecondaryContainer = Midnight,
    tertiary = DustyPink,
    onTertiary = Midnight,
    background = Midnight,
    onBackground = White,
    surface = Midnight,
    onSurface = White,
    surfaceVariant = SteelBlue,
    onSurfaceVariant = SkyBlue,
    surfaceContainerLowest = Midnight,
    surfaceContainerLow = Midnight,
    surfaceContainer = Navy,
    surfaceContainerHigh = Navy,
    surfaceContainerHighest = SteelBlue,
    outline = SkyBlue,
    outlineVariant = SteelBlue
)

// Dynamic (wallpaper) colour is turned off so the app always uses the ShelfLife palette.
@Composable
fun ShelfLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
