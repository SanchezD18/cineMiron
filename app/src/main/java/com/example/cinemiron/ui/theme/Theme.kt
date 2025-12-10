package com.example.cinemiron.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class ColorSchemeOption {
    VERDE, AZUL, PURPURA, ROJO, NARANJA, TEAL
}

private fun getDarkColorScheme(colorSchemeOption: ColorSchemeOption): ColorScheme {
    return when (colorSchemeOption) {
        ColorSchemeOption.VERDE -> darkColorScheme(
            primary = AccentLight,
            secondary = Accent,
            tertiary = PrimaryLight,
            background = PrimaryDark,
            surface = Primary,
            onPrimary = PrimaryDark,
            onSecondary = PrimaryDark,
            onTertiary = PrimaryDark,
            onBackground = AccentLight,
            onSurface = AccentLight,
        )
        ColorSchemeOption.AZUL -> darkColorScheme(
            primary = BlueAccentLight,
            secondary = BlueAccent,
            tertiary = BluePrimaryLight,
            background = BluePrimaryDark,
            surface = BluePrimary,
            onPrimary = BluePrimaryDark,
            onSecondary = BluePrimaryDark,
            onTertiary = BluePrimaryDark,
            onBackground = BlueAccentLight,
            onSurface = BlueAccentLight,
        )
        ColorSchemeOption.PURPURA -> darkColorScheme(
            primary = PurpleAccentLight,
            secondary = PurpleAccent,
            tertiary = PurplePrimaryLight,
            background = PurplePrimaryDark,
            surface = PurplePrimary,
            onPrimary = PurplePrimaryDark,
            onSecondary = PurplePrimaryDark,
            onTertiary = PurplePrimaryDark,
            onBackground = PurpleAccentLight,
            onSurface = PurpleAccentLight,
        )
        ColorSchemeOption.ROJO -> darkColorScheme(
            primary = RedAccentLight,
            secondary = RedAccent,
            tertiary = RedPrimaryLight,
            background = RedPrimaryDark,
            surface = RedPrimary,
            onPrimary = RedPrimaryDark,
            onSecondary = RedPrimaryDark,
            onTertiary = RedPrimaryDark,
            onBackground = RedAccentLight,
            onSurface = RedAccentLight,
        )
        ColorSchemeOption.NARANJA -> darkColorScheme(
            primary = OrangeAccentLight,
            secondary = OrangeAccent,
            tertiary = OrangePrimaryLight,
            background = OrangePrimaryDark,
            surface = OrangePrimary,
            onPrimary = OrangePrimaryDark,
            onSecondary = OrangePrimaryDark,
            onTertiary = OrangePrimaryDark,
            onBackground = OrangeAccentLight,
            onSurface = OrangeAccentLight,
        )
        ColorSchemeOption.TEAL -> darkColorScheme(
            primary = TealAccentLight,
            secondary = TealAccent,
            tertiary = TealPrimaryLight,
            background = TealPrimaryDark,
            surface = TealPrimary,
            onPrimary = TealPrimaryDark,
            onSecondary = TealPrimaryDark,
            onTertiary = TealPrimaryDark,
            onBackground = TealAccentLight,
            onSurface = TealAccentLight,
        )
    }
}

private fun getLightColorScheme(colorSchemeOption: ColorSchemeOption): ColorScheme {
    return when (colorSchemeOption) {
        ColorSchemeOption.VERDE -> lightColorScheme(
            primary = Primary,
            secondary = PrimaryDark,
            tertiary = PrimaryLight,
            background = AccentLight,
            surface = Accent,
            onPrimary = AccentLight,
            onSecondary = AccentLight,
            onTertiary = PrimaryDark,
            onBackground = PrimaryDark,
            onSurface = PrimaryDark
        )
        ColorSchemeOption.AZUL -> lightColorScheme(
            primary = BluePrimary,
            secondary = BluePrimaryDark,
            tertiary = BluePrimaryLight,
            background = BlueAccentLight,
            surface = BlueAccent,
            onPrimary = BlueAccentLight,
            onSecondary = BlueAccentLight,
            onTertiary = BluePrimaryDark,
            onBackground = BluePrimaryDark,
            onSurface = BluePrimaryDark
        )
        ColorSchemeOption.PURPURA -> lightColorScheme(
            primary = PurplePrimary,
            secondary = PurplePrimaryDark,
            tertiary = PurplePrimaryLight,
            background = PurpleAccentLight,
            surface = PurpleAccent,
            onPrimary = PurpleAccentLight,
            onSecondary = PurpleAccentLight,
            onTertiary = PurplePrimaryDark,
            onBackground = PurplePrimaryDark,
            onSurface = PurplePrimaryDark
        )
        ColorSchemeOption.ROJO -> lightColorScheme(
            primary = RedPrimary,
            secondary = RedPrimaryDark,
            tertiary = RedPrimaryLight,
            background = RedAccentLight,
            surface = RedAccent,
            onPrimary = RedAccentLight,
            onSecondary = RedAccentLight,
            onTertiary = RedPrimaryDark,
            onBackground = RedPrimaryDark,
            onSurface = RedPrimaryDark
        )
        ColorSchemeOption.NARANJA -> lightColorScheme(
            primary = OrangePrimary,
            secondary = OrangePrimaryDark,
            tertiary = OrangePrimaryLight,
            background = OrangeAccentLight,
            surface = OrangeAccent,
            onPrimary = OrangeAccentLight,
            onSecondary = OrangeAccentLight,
            onTertiary = OrangePrimaryDark,
            onBackground = OrangePrimaryDark,
            onSurface = OrangePrimaryDark
        )
        ColorSchemeOption.TEAL -> lightColorScheme(
            primary = TealPrimary,
            secondary = TealPrimaryDark,
            tertiary = TealPrimaryLight,
            background = TealAccentLight,
            surface = TealAccent,
            onPrimary = TealAccentLight,
            onSecondary = TealAccentLight,
            onTertiary = TealPrimaryDark,
            onBackground = TealPrimaryDark,
            onSurface = TealPrimaryDark
        )
    }
}

@Composable
fun CineMironTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorSchemeOption: ColorSchemeOption = ColorSchemeOption.VERDE,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> getDarkColorScheme(colorSchemeOption)
        else -> getLightColorScheme(colorSchemeOption)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}