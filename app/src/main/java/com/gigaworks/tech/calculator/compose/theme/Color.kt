package com.gigaworks.tech.calculator.compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.gigaworks.tech.calculator.util.AccentTheme

// Hex values mirror res/values/colors.xml and res/values-night/colors.xml.
// Keep in sync if either resource set changes.

private val BlueLight = Color(0xFF1A73E8)
private val BlueDark = Color(0xFF174EA6)
private val BlueClearLight = Color(0xFFD1E3FA)
private val BlueClearDark = Color(0xFF424547)

private val GreenLight = Color(0xFF4CAF50)
private val GreenDark = Color(0xFF388E3C)
private val GreenClearLight = Color(0xFFDBEFDC)

private val RedLight = Color(0xFFF44336)
private val RedDark = Color(0xFFD32F2F)
private val RedClearLight = Color(0xFFFDD9D7)

private val PurpleLight = Color(0xFF673AB7)
private val PurpleDark = Color(0xFF512DA8)
private val PurpleClearLight = Color(0xFFE1D8F1)

private val GreyLight = Color(0xFF607D8B)
private val GreyDark = Color(0xFF455A64)
private val GreyClearLight = Color(0xFFDFE5E8)

private val PinkLight = Color(0xFFE91E63)
private val PinkDark = Color(0xFFC2185B)
private val PinkClearLight = Color(0xFFFBD2E0)

// Shared neutral tokens.
// Dark values follow M3 elevation: each container token sits visibly above the one below it.
// The legacy XML set surfaceContainerHigh == background in dark mode, which only rendered correctly
// because Material You overrode it. With static accents the cards vanished — these new values
// keep cards visible across all accents.
private val SurfaceLight = Color(0xFFFFFFFF)
private val SurfaceDark = Color(0xFF202124)
private val SurfaceContainerLight = Color(0xFFF1F3F4)
private val SurfaceContainerDark = Color(0xFF2D3033)
private val SurfaceContainerHighLight = Color(0xFFECECEC)
private val SurfaceContainerHighDark = Color(0xFF3C4043)
private val BackgroundLight = Color(0xFFFFFFFF)
private val BackgroundDark = Color(0xFF202124)
private val OnSurfaceLight = Color(0xFF263238)
private val OnSurfaceDark = Color(0xFFFFFFFF)
private val OnSurfaceVariantLight = Color(0xFF3A444A)
private val OnSurfaceVariantDark = Color(0xFFE1E1E1)
private val OutlineLight = Color(0xFFDADCE0)
// Bumped from #3E3E3E so dividers stay visible against the new #3C4043 surfaceContainerHigh card.
private val OutlineDark = Color(0xFF5F6368)
private val ErrorLight = Color(0xFFE2363F)
private val ErrorDark = Color(0xFFE75E65)
private val OnPrimary = Color(0xFFFFFFFF)

private fun accentPrimary(accent: AccentTheme, dark: Boolean): Color = when (accent) {
    AccentTheme.BLUE -> if (dark) BlueDark else BlueLight
    AccentTheme.GREEN -> if (dark) GreenDark else GreenLight
    AccentTheme.RED -> if (dark) RedDark else RedLight
    AccentTheme.PURPLE -> if (dark) PurpleDark else PurpleLight
    AccentTheme.GREY -> if (dark) GreyDark else GreyLight
    AccentTheme.PINK -> if (dark) PinkDark else PinkLight
    AccentTheme.DYNAMIC -> if (dark) BlueDark else BlueLight // fallback when dynamic isn't available
}

private fun accentClear(accent: AccentTheme, dark: Boolean): Color = if (dark) {
    BlueClearDark
} else when (accent) {
    AccentTheme.BLUE -> BlueClearLight
    AccentTheme.GREEN -> GreenClearLight
    AccentTheme.RED -> RedClearLight
    AccentTheme.PURPLE -> PurpleClearLight
    AccentTheme.GREY -> GreyClearLight
    AccentTheme.PINK -> PinkClearLight
    AccentTheme.DYNAMIC -> BlueClearLight
}

fun buildColorScheme(accent: AccentTheme, dark: Boolean): ColorScheme {
    val primary = accentPrimary(accent, dark)
    val clear = accentClear(accent, dark)
    return if (dark) {
        darkColorScheme(
            primary = primary,
            onPrimary = OnPrimary,
            primaryContainer = clear,
            onPrimaryContainer = OnSurfaceDark,
            surface = SurfaceDark,
            onSurface = OnSurfaceDark,
            surfaceContainer = SurfaceContainerDark,
            surfaceContainerHigh = SurfaceContainerHighDark,
            background = BackgroundDark,
            onBackground = OnSurfaceDark,
            onSurfaceVariant = OnSurfaceVariantDark,
            outlineVariant = OutlineDark,
            error = ErrorDark,
            onError = OnPrimary,
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = OnPrimary,
            primaryContainer = clear,
            onPrimaryContainer = OnSurfaceLight,
            surface = SurfaceLight,
            onSurface = OnSurfaceLight,
            surfaceContainer = SurfaceContainerLight,
            surfaceContainerHigh = SurfaceContainerHighLight,
            background = BackgroundLight,
            onBackground = OnSurfaceLight,
            onSurfaceVariant = OnSurfaceVariantLight,
            outlineVariant = OutlineLight,
            error = ErrorLight,
            onError = OnPrimary,
        )
    }
}
