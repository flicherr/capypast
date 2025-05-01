package com.capypast.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.capypast.R

@Composable
fun ThemedBackground(
    modifier: Modifier = Modifier,
) {
    val backgroundPainter = painterResource(
        id = if (isSystemInDarkTheme()) R.drawable.bg_dark else R.drawable.bg_light
    )
    Image(
        painter = backgroundPainter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}