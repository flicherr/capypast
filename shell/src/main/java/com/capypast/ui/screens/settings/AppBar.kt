package com.capypast.ui.screens.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.capypast.ui.components.ButtonBack
import com.capypast.ui.components.ButtonToTrashcan
import com.capypast.ui.components.TitleAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	onClickToTrashcan: () -> Unit,
	onClickToBack: () -> Unit,
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
		navigationIcon = { ButtonBack(onClickToBack) },
		title = { TitleAppBar(title) },
		actions = { ButtonToTrashcan(onClickToTrashcan) },
		colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
	)
}