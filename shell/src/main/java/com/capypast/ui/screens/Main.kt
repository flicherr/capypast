package com.capypast.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capypast.R
import com.capypast.room.ClipboardDatabase
import com.capypast.room.ClipboardRepository
import com.capypast.ui.appbar.AppBar
import com.capypast.ui.fragments.History
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.ClipboardViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    onClickToSettings: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember {
        val db = ClipboardDatabase.getInstance(context)
        ClipboardRepository(db.clipboardDao())
    }
    val viewModel: ClipboardViewModel = viewModel(
        factory = ClipboardViewModelFactory(repository)
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
                onSettingsClick = onClickToSettings,
                onSearch = { query -> viewModel.search(query) }
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            History(viewModel)
        }
    }
}