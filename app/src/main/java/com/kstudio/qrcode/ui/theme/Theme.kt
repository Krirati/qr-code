package com.kstudio.qrcode.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreyLightDark,
    primaryContainer = GreyLightActive,
    secondary = GreyLightDarkHover,
    secondaryContainer = GreyLightNormalActive,
    tertiary = GreyLightDarkActive,
    onTertiaryContainer = GreyLightNormal,
    background = GreyLightNormalActive,
    onBackground = GreyLightDark,
    outline = GreyLightActive
)

private val LightColorScheme = lightColorScheme(
    primary = GreyLightDarker,
    primaryContainer = GreyLightDarkActive,
    secondary = GreyLightDark,
    secondaryContainer = GreyLightDarkHover,
    tertiary = GreyLightDarker,
    onTertiaryContainer = GreyLightNormal,
    background = GreyLightNormalActive,
    onBackground = GreyLightDark,
    outline = GreyLightActive
)

@Composable
fun QrCodeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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