package com.capypast.ui.screens.trashcan

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
		containerColor = Color.Transparent,
	) { padding ->
		Surface(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize(),
			color = Color.Transparent,
		) {
			Column {

				HorizontalDivider(
					thickness = 1.dp,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
				)
				TrashList()
			}
		}
	}
}