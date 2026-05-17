package com.gigaworks.tech.calculator.compose.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gigaworks.tech.calculator.util.AccentTheme

val LocalAccentTheme = staticCompositionLocalOf { AccentTheme.BLUE }

@Composable
fun CalculatorPlusTheme(
    accent: AccentTheme = AccentTheme.BLUE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val targetScheme: ColorScheme = when {
        accent == AccentTheme.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> buildColorScheme(accent, darkTheme)
    }

    // Animate each token so accent / light-dark switches glide instead of snapping.
    val animatedScheme = targetScheme.animated()

    CompositionLocalProvider(LocalAccentTheme provides accent) {
        MaterialTheme(
            colorScheme = animatedScheme,
            typography = CalculatorTypography,
            shapes = CalculatorShapes,
            content = content,
        )
    }
}

@Composable
private fun ColorScheme.animated(): ColorScheme {
    val spec = spring<Color>()
    return copy(
        primary = animateColorAsState(primary, spec, label = "primary").value,
        onPrimary = animateColorAsState(onPrimary, spec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(primaryContainer, spec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer, spec, label = "onPrimaryContainer").value,
        surface = animateColorAsState(surface, spec, label = "surface").value,
        onSurface = animateColorAsState(onSurface, spec, label = "onSurface").value,
        surfaceContainer = animateColorAsState(surfaceContainer, spec, label = "surfaceContainer").value,
        surfaceContainerHigh = animateColorAsState(surfaceContainerHigh, spec, label = "surfaceContainerHigh").value,
        background = animateColorAsState(background, spec, label = "background").value,
        onBackground = animateColorAsState(onBackground, spec, label = "onBackground").value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant, spec, label = "onSurfaceVariant").value,
        outlineVariant = animateColorAsState(outlineVariant, spec, label = "outlineVariant").value,
        error = animateColorAsState(error, spec, label = "error").value,
        onError = animateColorAsState(onError, spec, label = "onError").value,
    )
}
