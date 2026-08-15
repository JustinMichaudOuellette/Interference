package ca.justinmo.interference.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = White,
  onPrimary = Black,
  primaryContainer = DarkGrey,
  onPrimaryContainer = White,
  secondary = Grey,
  onSecondary = White,
  secondaryContainer = DarkGrey,
  onSecondaryContainer = White,
  tertiary = LightGrey,
  onTertiary = Black,
  background = Color.Black,
  surface = Color.Black,
  onBackground = Color.White,
  onSurface = Color.White,
  surfaceVariant = DarkGrey,
  onSurfaceVariant = LightGrey
)

private val LightColorScheme = lightColorScheme(
  primary = Black,
  onPrimary = White,
  primaryContainer = LightGrey,
  onPrimaryContainer = Black,
  secondary = DarkGrey,
  onSecondary = White,
  secondaryContainer = LightGrey,
  onSecondaryContainer = Black,
  tertiary = Grey,
  onTertiary = White,
  background = Color.White,
  surface = Color.White,
  onBackground = Color.Black,
  onSurface = Color.Black,
  surfaceVariant = LightGrey,
  onSurfaceVariant = DarkGrey
)

@Composable
fun JustinTheme(
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

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
