package com.capypast.ui.screens.clipboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.capypast.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Clipboard(
	onClickToSettings: () -> Unit
) {
	Scaffold(
		topBar = {
			AppBar(
				title = stringResource(R.string.app_name),
				onClickToSettings = onClickToSettings,
			)
		},
		containerColor = Color.Transparent,
	) { padding ->
		Surface(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.imePadding(),
			color = Color.Transparent,
		) {
			Column {
				HorizontalDivider(
					thickness = 1.dp,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
				)
				ClipList()
			}
		}
	}
}