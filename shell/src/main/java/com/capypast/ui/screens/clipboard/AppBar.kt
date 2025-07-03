package com.capypast.ui.screens.clipboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.capypast.ui.components.ButtonCloseSearch
import com.capypast.ui.components.ButtonSearch
import com.capypast.ui.components.ButtonToSettings
import com.capypast.ui.components.ConfirmDialog
import com.capypast.ui.components.TitleAppBar
import com.capypast.viewmodel.ClipboardViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Search
import compose.icons.tablericons.SquareCheck
import compose.icons.tablericons.SquareDot
import compose.icons.tablericons.Trash
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToSettings: () -> Unit,
) {
	var query: String? by remember { mutableStateOf(null) }
	val focusRequester = remember { FocusRequester() }
	val keyboardCtrl = LocalSoftwareKeyboardController.current

	var confirmRemove by remember { mutableStateOf(false) }

	val viewModel: ClipboardViewModel = koinViewModel()

	BackHandler(enabled = viewModel.selectionMode()) {
		viewModel.clearSelection()
	}

	BackHandler(enabled = viewModel.searchBarAction()) {
		viewModel.toggleSearchBarAction()
	}

	LaunchedEffect(viewModel.searchBarAction()) {
		if (viewModel.searchBarAction()) {
			focusRequester.requestFocus()
			keyboardCtrl?.show()
		} else {
			keyboardCtrl?.hide()
			query = ""
		}
	}

	ConfirmDialog(
		show = confirmRemove,
		message =
			"Вы действительно хотите переместить выбранные элементы в корзину?",
		onConfirm = {
			viewModel.deleteSelected()
		},
		onDismiss = {
			confirmRemove = false
		}
	)

	TopAppBar(
		title = {
			when {
				viewModel.selectionMode() -> {
					Text(
						text = "${viewModel.selectedItems.size} выбрано"
							.takeIf { viewModel.selectionMode() } ?: "История"
					)
				}

				viewModel.searchBarAction() -> {
					OutlinedTextField(
						value = query.toString(),
						onValueChange = { query = it },
						placeholder = { Text("поиск...") },
						singleLine = true,
						modifier = Modifier
							.fillMaxWidth()
							.focusRequester(focusRequester)
							.clip(RoundedCornerShape(12.dp)),
						shape = RoundedCornerShape(12.dp),
						colors = OutlinedTextFieldDefaults.colors(
							unfocusedBorderColor = Color.Transparent,
							focusedBorderColor = Color.Transparent,
							cursorColor = MaterialTheme.colorScheme.primary,
						),
						leadingIcon = {
							Icon(
								TablerIcons.Search, null,
								tint = MaterialTheme.colorScheme.primary
							)
						},
						keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
						keyboardActions = KeyboardActions(onSearch = {
							viewModel.toggleSearchBarAction()
							viewModel.search(query.toString())
						})
					)
				}

				else -> TitleAppBar(title)
			}
		},
		actions = {
			when {
				viewModel.selectionMode() -> {
					IconButton(
						onClick = {
							viewModel.selectAll()
						}
					) {
						Icon(
							imageVector = TablerIcons.SquareCheck,
							contentDescription = "Выбрать всё",
						)
					}

					IconButton(
						onClick = {
							viewModel.clearSelection()
						}
					) {
						Icon(
							imageVector = TablerIcons.SquareDot,
							contentDescription = "Снять выделение",
						)
					}

					IconButton(
						onClick = {
							confirmRemove = true
						}
					) {
						Icon(
							imageVector = TablerIcons.Trash,
							contentDescription = "В корзину",
						)
					}
				}

				viewModel.searchBarAction() -> {
					ButtonCloseSearch { viewModel.toggleSearchBarAction() }
				}

				else -> {
					ButtonSearch { viewModel.toggleSearchBarAction() }
					ButtonToSettings(onClickToSettings)
				}
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
	)
}