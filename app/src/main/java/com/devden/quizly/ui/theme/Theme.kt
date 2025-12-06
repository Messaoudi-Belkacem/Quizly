package com.devden.quizly.ui.theme

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
    primary = ElectricBlue60,
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D61),
    onPrimaryContainer = Color(0xFFBEE9FF),
    secondary = VibrantPurple60,
    onSecondary = Color(0xFF3B2948),
    secondaryContainer = Color(0xFF523B5F),
    onSecondaryContainer = Color(0xFFEFDBFF),
    tertiary = VibrantTeal60,
    onTertiary = Color(0xFF003737),
    tertiaryContainer = Color(0xFF004F50),
    onTertiaryContainer = Color(0xFFA6F2F1),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFC0C8CD),
    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484C),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = ElectricBlue60,
    surfaceDim = Color(0xFF1A1C1E),
    surfaceBright = Color(0xFF404244),
    surfaceContainerLowest = Color(0xFF0F1113),
    surfaceContainerLow = Color(0xFF1A1C1E),
    surfaceContainer = Color(0xFF1E2022),
    surfaceContainerHigh = Color(0xFF282A2D),
    surfaceContainerHighest = Color(0xFF333538)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue60,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBEE9FF),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = VibrantPurple60,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFDBFF),
    onSecondaryContainer = Color(0xFF25003A),
    tertiary = VibrantTeal60,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA6F2F1),
    onTertiaryContainer = Color(0xFF00201F),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFCFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDDE3E9),
    onSurfaceVariant = Color(0xFF40484C),
    outline = Color(0xFF71787D),
    outlineVariant = Color(0xFFC0C8CD),
    scrim = Color.Black,
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFF6DD3FF),
    surfaceDim = Color(0xFFD9D9DD),
    surfaceBright = Color(0xFFFCFCFF),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F3F7),
    surfaceContainer = Color(0xFFEDEDF1),
    surfaceContainerHigh = Color(0xFFE7E8EB),
    surfaceContainerHighest = Color(0xFFE2E2E6)
)

@Composable
fun QuizlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}