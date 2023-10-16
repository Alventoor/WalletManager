package com.github.alventoor.walletmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Blue40,
    primaryVariant = PastelBlue40,
    secondary = Orange80,
    secondaryVariant = Orange70,
    background = Color(0xfff8fdff),
    surface = Color(0xfff8fdff),
    error = Red40,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xff001f25),
    onSurface = Color(0xff001f25),
    onError = Color.White
)

private val DarkColors = darkColors(
    primary = Blue80,
    primaryVariant = DuckBlue40,
    secondary = Orange70,
    secondaryVariant = Orange70,
    background = Color(0xff000e18),
    surface = PastelBlue10,
    error = Red80,
    onPrimary = Blue20,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Red20
)

@Composable
fun AppTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable() () -> Unit
) {
  MaterialTheme(
      colors = if (useDarkTheme) DarkColors else LightColors,
      content = content
  )
}