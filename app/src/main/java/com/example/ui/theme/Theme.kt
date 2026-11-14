package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AetherPalette(
    val obsidianVoid: Color = ObsidianVoid,
    val tacticalNavy: Color = TacticalNavy,
    val glassSurface: Color = GlassSurface,
    val glassSurfaceElevated: Color = GlassSurfaceElevated,
    val glassSurfaceBright: Color = GlassSurfaceBright,
    val gridLineSubtle: Color = GridLineSubtle,
    val gridLineAccent: Color = GridLineAccent,
    val glassBorder: Color = GlassBorder,
    val glassBorderStrong: Color = GlassBorderStrong,
    val nominalGreen: Color = NominalGreen,
    val nominalGreenSurface: Color = NominalGreenSurface,
    val telemetryCyan: Color = TelemetryCyan,
    val telemetryCyanSurface: Color = TelemetryCyanSurface,
    val cautionAmber: Color = CautionAmber,
    val cautionAmberSurface: Color = CautionAmberSurface,
    val criticalRed: Color = CriticalRed,
    val criticalRedSurface: Color = CriticalRedSurface,
    val radiationViolet: Color = RadiationViolet,
    val radiationVioletSurface: Color = RadiationVioletSurface,
    val ecgGridMinor: Color = EcgGridMinor,
    val ecgGridMajor: Color = EcgGridMajor,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
)

val LocalAetherPalette = staticCompositionLocalOf { AetherPalette() }

private val AetherDarkColorScheme = darkColorScheme(
    primary = TelemetryCyan,
    onPrimary = ObsidianVoid,
    primaryContainer = TelemetryCyanSurface,
    onPrimaryContainer = TelemetryCyan,
    secondary = NominalGreen,
    onSecondary = ObsidianVoid,
    secondaryContainer = NominalGreenSurface,
    onSecondaryContainer = NominalGreen,
    tertiary = RadiationViolet,
    onTertiary = ObsidianVoid,
    background = ObsidianVoid,
    onBackground = TextPrimary,
    surface = TacticalNavy,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderStrong,
    error = CriticalRed,
    onError = ObsidianVoid,
    errorContainer = CriticalRedSurface,
    onErrorContainer = CriticalRed,
)

@Composable
fun AetherTheme(
    content: @Composable () -> Unit,
) {
    val palette = AetherPalette()
    androidx.compose.runtime.CompositionLocalProvider(LocalAetherPalette provides palette) {
        MaterialTheme(
            colorScheme = AetherDarkColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
