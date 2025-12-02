package be.he2b.scoutpocket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = LightAccent,
    onPrimary = Color(0xFFFFFFFF),

    // Primary Surfaces
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightPrimarySurface,
    onSurface = LightSecondaryText,

    // Secondary Surfaces
    secondaryContainer = LightSecondarySurface,
    onSecondaryContainer = LightAccent,

    // Error
    error = StateAbsentBackground,
    onError = StateAbsentContent,
)

private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = DarkAccent,
    onPrimary = Color(0xFFFFFFFF),

    // Primary Surfaces
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkPrimarySurface,
    onSurface = DarkSecondaryText,

    // Secondary Surfaces
    secondaryContainer = DarkSecondarySurface,
    onSecondaryContainer = LightAccent,

    // Error
    error = StateAbsentBackground,
    onError = StateAbsentContent,
)

@Composable
fun ScoutPocketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}