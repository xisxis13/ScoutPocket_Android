package be.he2b.scoutpocket.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = PrimaryPurple,
    onPrimary = SurfaceBox,

    // Primary Surfaces
    background = BackgroundPrimary,
    onBackground = DarkPurple,
    surface = SurfaceBox,
    onSurface = DarkPurple,

    // Secondary
    secondary = PrimaryPurple,
    onSecondary = DarkPurple,

    // Secondary Surfaces
    secondaryContainer = LightPurple,
    onSecondaryContainer = PrimaryPurple,

    // Error
    error = StateAbsentBackground,
    onError = StateAbsentContent,
)

private val DarkColorScheme = darkColorScheme(
    primary = LightPurple,
    onPrimary = DarkPurple,
    background = Color(0xFF121212),
    onBackground = BackgroundPrimary,
    surface = Color(0xFF1D1D1D),
    onSurface = BackgroundPrimary
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