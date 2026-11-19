package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val AzkariLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = GoldSecondary,
    onSecondary = GoldOnSecondary,
    secondaryContainer = GoldSecondaryContainer,
    onSecondaryContainer = GoldOnSecondaryContainer,
    tertiary = SageTertiary,
    onTertiary = SageOnTertiary,
    tertiaryContainer = SageTertiaryContainer,
    onTertiaryContainer = SageOnTertiaryContainer,
    background = IvoryBackground,
    onBackground = IvoryOnBackground,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmOnSurfaceVariant,
    outline = SoftOutline,
    error = ErrorCrimson
)

private val AzkariDarkColorScheme = darkColorScheme(
    primary = NightEmeraldPrimary,
    onPrimary = NightEmeraldOnPrimary,
    primaryContainer = NightEmeraldPrimaryContainer,
    onPrimaryContainer = NightEmeraldOnPrimaryContainer,
    secondary = NightGoldSecondary,
    onSecondary = NightGoldOnSecondary,
    secondaryContainer = NightGoldSecondaryContainer,
    onSecondaryContainer = NightGoldOnSecondaryContainer,
    tertiary = NightSageTertiary,
    onTertiary = NightSageOnTertiary,
    tertiaryContainer = NightSageTertiaryContainer,
    onTertiaryContainer = NightSageOnTertiaryContainer,
    background = NightBackground,
    onBackground = NightOnBackground,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightOnSurfaceVariant,
    outline = NightOutline,
    error = NightErrorCrimson
)

val AzkariShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun AzkariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AzkariDarkColorScheme else AzkariLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AzkariShapes,
        content = content
    )
}
