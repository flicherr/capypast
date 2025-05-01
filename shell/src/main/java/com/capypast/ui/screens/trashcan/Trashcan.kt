package com.capypast.ui.screens.trashcan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capypast.room.ClipboardDatabase
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.room.repositories.TrashRepository
import com.capypast.viewmodel.TrashViewModel
import com.capypast.viewmodel.factories.TrashViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trashcan(
    onClickToBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        val context = LocalContext.current
        val db = ClipboardDatabase.getInstance(context)
        val repository = remember {
            TrashRepository(db.trashDao())
        }
        val restore = RestoreFromTrashInteractor(
            clipboardRepo = ClipboardRepository(db.clipboardDao()),
            trashRepo = repository,
            db = db,
        )
        val viewModel: TrashViewModel = viewModel(
            factory = TrashViewModelFactory(
                repository,
                restore
            )
        )

        Column {
            AppBar(
                title = "Корзина",
                onClickToBack = onClickToBack,
                viewModel = viewModel
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            TrashList(viewModel)
        }
    }
}