package com.drapefullness.reckoner.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drapefullness.reckoner.core.di.ServiceLocator

@Immutable
data class AppColors(
    val gradientTop: Color,
    val gradientMid: Color,
    val gradientBottom: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val hairline: Color,
    val primary: Color,
    val primaryDeep: Color,
    val brass: Color,
    val brassLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val onPrimary: Color,
    val error: Color,
    val success: Color,
    val dark: Boolean
)

private val DayColors = AppColors(
    gradientTop = LinenHigh,
    gradientMid = LinenBase,
    gradientBottom = LinenDeep,
    surface = PaperWhite,
    surfaceVariant = LinenVariant,
    hairline = HairlineLight,
    primary = TealPrimary,
    primaryDeep = TealDeep,
    brass = Brass,
    brassLight = BrassLight,
    textPrimary = InkPrimary,
    textSecondary = InkSecondary,
    textTertiary = InkTertiary,
    onPrimary = OnTeal,
    error = ClayError,
    success = SageSuccess,
    dark = false
)

private val EveningColors = AppColors(
    gradientTop = NightHigh,
    gradientMid = NightBase,
    gradientBottom = NightDeep,
    surface = NightSurface,
    surfaceVariant = NightVariant,
    hairline = HairlineDark,
    primary = TealSoft,
    primaryDeep = TealSoftDeep,
    brass = Brass,
    brassLight = BrassLight,
    textPrimary = MoonPrimary,
    textSecondary = MoonSecondary,
    textTertiary = MoonTertiary,
    onPrimary = NightBase,
    error = ClayError,
    success = SageSuccess,
    dark = true
)

private val DayScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = OnTeal,
    primaryContainer = TealDeep,
    onPrimaryContainer = OnTeal,
    secondary = TealDeep,
    onSecondary = OnTeal,
    tertiary = Brass,
    onTertiary = InkPrimary,
    background = LinenBase,
    onBackground = InkPrimary,
    surface = PaperWhite,
    onSurface = InkPrimary,
    surfaceVariant = LinenVariant,
    onSurfaceVariant = InkSecondary,
    outline = HairlineLight,
    outlineVariant = HairlineLight,
    error = ClayError,
    onError = OnTeal
)

private val EveningScheme = darkColorScheme(
    primary = TealSoft,
    onPrimary = NightBase,
    primaryContainer = TealSoftDeep,
    onPrimaryContainer = MoonPrimary,
    secondary = TealSoftDeep,
    onSecondary = MoonPrimary,
    tertiary = Brass,
    onTertiary = NightBase,
    background = NightBase,
    onBackground = MoonPrimary,
    surface = NightSurface,
    onSurface = MoonPrimary,
    surfaceVariant = NightVariant,
    onSurfaceVariant = MoonSecondary,
    outline = HairlineDark,
    outlineVariant = HairlineDark,
    error = ClayError,
    onError = NightBase
)

val LocalAppColors = staticCompositionLocalOf { DayColors }

val LocalHapticsEnabled = compositionLocalOf { true }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val evening by ServiceLocator.eveningTheme.collectAsStateWithLifecycle()
    val haptics by ServiceLocator.haptics.collectAsStateWithLifecycle()
    val palette = if (evening) EveningColors else DayColors
    CompositionLocalProvider(
        LocalAppColors provides palette,
        LocalHapticsEnabled provides haptics
    ) {
        MaterialTheme(
            colorScheme = if (evening) EveningScheme else DayScheme,
            typography = AppTypography,
            content = content
        )
    }
}
