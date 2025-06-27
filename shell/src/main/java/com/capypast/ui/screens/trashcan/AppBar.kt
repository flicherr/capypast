package com.capypast.ui.screens.trashcan

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capypast.ui.components.ButtonRestoreAll
import com.capypast.ui.components.ButtonBack
import com.capypast.ui.components.ButtonClearTrashcan
import com.capypast.ui.components.ConfirmDialog
import com.capypast.ui.components.TitleAppBar
import com.capypast.viewmodel.TrashViewModel
import com.capypast.viewmodel.factories.TrashViewModelFactory
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToBack: () -> Unit,
) {
	val scope = rememberCoroutineScope()
	var confirmClearAll by remember { mutableStateOf(false) }
	var confirmRestoreAll by remember { mutableStateOf(false) }

	val context = LocalContext.current
	val viewModel: TrashViewModel = viewModel(
		factory = TrashViewModelFactory(context)
	)

	ConfirmDialog(
		show = confirmRestoreAll,
		message =
			"Вы действительно хотите восстановить все элементы из корзины в историю?",
		onConfirm = {
			viewModel.restoreAll()
			scope.launch {
//				snackbarHostState.showSnackbar(
//					message = "Все элементы корзины восстановлены",
//					withDismissAction = true,
//				)
			}
		},
		onDismiss = {
			confirmRestoreAll = false
		}
	)
	ConfirmDialog(
		show = confirmClearAll,
		message =
			"Вы действительно хотите безвозвратно удалить все элементы корзины?",
		onConfirm = {
			viewModel.deleteAll()
			scope.launch {
//				snackbarHostState.showSnackbar(
//					message = "Все элементы корзины удалены",
//					withDismissAction = true,
//				)
			}
		},
		onDismiss = {
			confirmClearAll = false
		}
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