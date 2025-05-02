package com.capypast.ui.screens.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.capypast.ui.components.TitleAppBar
import compose.icons.TablerIcons
import compose.icons.tablericons.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToSettings: () -> Unit,
	onClickToSearch: (String) -> Unit,
) {
	var isSearchActive by remember { mutableStateOf(false) }
	var query by remember { mutableStateOf("") }

	val focusRequester = remember { FocusRequester() }
	val keyboardCtrl = LocalSoftwareKeyboardController.current

	LaunchedEffect(isSearchActive) {
		if (isSearchActive) {
			focusRequester.requestFocus()
			keyboardCtrl?.show()
		} else {
			keyboardCtrl?.hide()
			query = ""
		}
	}
	TopAppBar(
		title = {
			if (isSearchActive) {
				OutlinedTextField(
					value = query,
					onValueChange = { query = it },
					placeholder = { Text("поиск…") },
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
						onClickToSearch(query)
						isSearchActive = false
					})
				)
			} else {
				TitleAppBar(title)
			}
		},
		actions = {
			if (isSearchActive) {
				ButtonCloseSearch{ isSearchActive = false }
			} else {
				ButtonSearch { isSearchActive = true }
				ButtonToSettings(onClickToSettings)
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
	)
}