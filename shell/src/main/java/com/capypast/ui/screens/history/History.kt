package com.capypast.ui.screens.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capypast.R
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.factories.ClipboardViewModelFactory

@Composable
fun History(
	onClickToSettings: () -> Unit
) {
	val context = LocalContext.current
	val viewModel: ClipboardViewModel = viewModel(
		factory = ClipboardViewModelFactory(context)
	)

	Surface(
		modifier = Modifier
			.fillMaxSize()
			.imePadding(),
		color = Color.Transparent,
	) {
		Column {
			AppBar(
				title = stringResource(R.string.app_name),
				onClickToSettings = onClickToSettings,
				onClickToSearch = { query -> viewModel.search(query) }
			)
			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
			)

			HistoryList(viewModel)
		}
	}
}