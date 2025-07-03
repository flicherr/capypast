package com.capypast.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
	primary = PrimaryDark,
	secondary = SecondaryDark,
	tertiary = TertiaryDark,
	surface = SurfaceDark
)

private val LightColorScheme = lightColorScheme(
	primary = PrimaryLight,
	secondary = SecondaryLight,
	tertiary = TertiaryLight,
	surface = SurfaceLight

	/* Other default colors to override
	background = Color(0xFFFFFBFE),
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
	*/
)

@Composable
fun CapypastTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
//	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
//		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//			val context = LocalContext.current
//			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//		}

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
fun TransparentTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
//	dynamicColor: Boolean = true
) {
	val colorScheme = when {
//		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//			val context = LocalContext.current
//			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//		}

		darkTheme -> darkColorScheme(
			surface = Color.Transparent,
			background = Color.Transparent
		)
		else -> lightColorScheme(
			surface = Color.Transparent,
			background = Color.Transparent
		)
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography
	) {
		Box(
			Modifier
				.fillMaxSize()
				.background(Color.Transparent)
		)
	}
}