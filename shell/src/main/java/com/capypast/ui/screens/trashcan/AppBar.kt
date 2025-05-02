package com.capypast.ui.screens.trashcan

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.capypast.ui.components.ButtonRestoreAll
import com.capypast.ui.components.ButtonBack
import com.capypast.ui.components.ButtonClearTrashcan
import com.capypast.ui.components.TitleAppBar
import com.capypast.viewmodel.TrashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToBack: () -> Unit,
	viewModel: TrashViewModel
) {
	TopAppBar(
		navigationIcon = { ButtonBack(onClickToBack) },
		title = { TitleAppBar(title) },
		actions = {
			ButtonRestoreAll{ viewModel.onRestoreAll() }
			ButtonClearTrashcan { viewModel.onClearAll() }
		},
		colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
	)
}