package com.capypast.ui.screens.trashcan

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.capypast.ui.components.ButtonRestoreAll
import com.capypast.ui.components.ButtonBack
import com.capypast.ui.components.ButtonClearTrashcan
import com.capypast.ui.components.ConfirmDialog
import com.capypast.ui.components.TitleAppBar
import com.capypast.viewmodel.TrashViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToBack: () -> Unit,
) {
	var confirmClearAll by remember { mutableStateOf(false) }
	var confirmRestoreAll by remember { mutableStateOf(false) }

	val viewModel: TrashViewModel = koinViewModel()

	ConfirmDialog(
		show = confirmRestoreAll,
		message =
			"Вы действительно хотите восстановить все элементы из корзины в историю?",
		onConfirm = { viewModel.restoreAll() },
		onDismiss = { confirmRestoreAll = false }
	)
	ConfirmDialog(
		show = confirmClearAll,
		message =
			"Вы действительно хотите безвозвратно удалить все элементы корзины?",
		onConfirm = { viewModel.deleteAll() },
		onDismiss = { confirmClearAll = false }
	)

	TopAppBar(
		navigationIcon = { ButtonBack(onClickToBack) },
		title = { TitleAppBar(title) },
		actions = {
			ButtonRestoreAll { confirmRestoreAll = true }
			ButtonClearTrashcan { confirmClearAll = true }
		},
		colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
	)
}