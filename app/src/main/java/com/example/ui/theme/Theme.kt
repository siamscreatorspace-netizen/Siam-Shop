package com.example.ui.theme

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
  primary = SiamPrimary,
  onPrimary = SiamOnPrimaryDark,
  primaryContainer = SiamPrimaryVariant,
  secondary = SiamSecondary,
  onSecondary = SiamOnPrimaryDark,
  secondaryContainer = SiamSecondaryLight,
  tertiary = SiamTertiary,
  background = SiamBackgroundDark,
  surface = SiamSurfaceDark,
  surfaceVariant = SiamSurfaceVariantDark,
  onBackground = SiamOnSurfaceDark,
  onSurface = SiamOnSurfaceDark,
)

private val LightColorScheme = lightColorScheme(
  primary = SiamPrimary,
  onPrimary = SiamOnPrimaryLight,
  primaryContainer = SiamPrimaryVariant,
  secondary = SiamSecondary,
  onSecondary = SiamOnPrimaryLight,
  secondaryContainer = SiamSecondaryLight,
  tertiary = SiamTertiary,
  background = SiamBackgroundLight,
  surface = SiamSurfaceLight,
  surfaceVariant = SiamSurfaceVariantLight,
  onBackground = SiamOnSurfaceLight,
  onSurface = SiamOnSurfaceLight,
)

@Composable
fun SiamShopTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted Siam Shop brand colors for identity
  content: @Composable () -> Unit,
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

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  SiamShopTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

