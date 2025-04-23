package com.capypast.ui.appbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    onSettingsClick: () -> Unit,
    onSearch: (String) -> Unit,
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
                            Icons.Rounded.Search, null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearch(query)
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
                ButtonSettings(onSettingsClick)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
    )
}

@Composable
fun TitleAppBar(title: String, modifier: Modifier = Modifier) {
    Row {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
            style = MaterialTheme.typography.labelSmall
                .copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun ButtonCloseSearch(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Rounded.Close,
            contentDescription = "Закрыть поиск",
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ButtonSearch(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Rounded.Search,
            contentDescription = "Поиск",
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ButtonSettings(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Настройки",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun ButtonBack(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Назад",
            modifier = Modifier.size(28.dp)
        )
    }
}