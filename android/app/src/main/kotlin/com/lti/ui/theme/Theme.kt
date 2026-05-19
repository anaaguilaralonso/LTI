package com.lti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = LtiBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LtiBlueLight,
    secondary = LtiBlueDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = LtiBlueLight,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = LtiBlue,
    secondary = LtiBlueLight,
)

@Composable
fun LtiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
