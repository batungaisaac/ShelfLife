package com.example.shelflife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Navy is primary (top bar, buttons), pink is secondary/tertiary (FAB, selected chips,
// nav indicator), white is the background.
private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = White,
    primaryContainer = NavyLight,
    onPrimaryContainer = Navy,
    secondary = Pink,
    onSecondary = White,
    secondaryContainer = PinkLight,
    onSecondaryContainer = PinkDeep,
    tertiary = Pink,
    onTertiary = White,
    tertiaryContainer = PinkLight,
    onTertiaryContainer = PinkDeep,
    background = White,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = CoolGrey,
    onSurfaceVariant = Slate,
    surfaceContainerLowest = White,
    surfaceContainerLow = OffWhite,
    surfaceContainer = OffWhite,
    surfaceContainerHigh = CoolGrey,
    surfaceContainerHighest = CoolGrey,
    outline = Slate
)

private val DarkColorScheme = darkColorScheme(
    primary = NavySoft,
    onPrimary = NavyDark,
    primaryContainer = Navy,
    onPrimaryContainer = NavyLight,
    secondary = PinkSoft,
    onSecondary = PinkDeep,
    secondaryContainer = Pink,
    onSecondaryContainer = White,
    tertiary = PinkSoft,
    onTertiary = PinkDeep,
    background = NavyDark,
    onBackground = White,
    surface = NavyDark,
    onSurface = White,
    surfaceVariant = Navy,
    onSurfaceVariant = NavySoft,
    surfaceContainerLowest = NavyDark,
    surfaceContainerLow = NavyDark,
    surfaceContainer = Navy,
    surfaceContainerHigh = Navy,
    surfaceContainerHighest = Navy
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
