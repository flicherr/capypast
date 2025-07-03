package com.capypast.ui.screens.trashcan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Trashcan(
	onClickToBack: () -> Unit
) {
	Scaffold(
		topBar = {
			AppBar(
				title = "корзина",
				onClickToBack = onClickToBack,
			)
		},
		containerColor = Color.Transparent
	) { padding ->
		Column(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.imePadding(),
		) {
			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
			)
			TrashList()
		}
	}
}