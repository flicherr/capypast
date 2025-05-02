package com.capypast.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capypast.viewmodel.TrashViewModel
import com.capypast.viewmodel.factories.TrashViewModelFactory
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.RotateClockwise
import compose.icons.tablericons.Search
import compose.icons.tablericons.Settings
import compose.icons.tablericons.SquareX
import compose.icons.tablericons.Trash
import compose.icons.tablericons.X

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
			TablerIcons.X,
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
			TablerIcons.Search,
			contentDescription = "Поиск",
			modifier = Modifier.size(28.dp),
			tint = MaterialTheme.colorScheme.primary
		)
	}
}

@Composable
fun ButtonToSettings(onClick: () -> Unit) {
	IconButton(
		onClick = onClick,
	) {
		Icon(
			TablerIcons.Settings,
			contentDescription = "Настройки",
			modifier = Modifier.size(28.dp)
		)
	}
}

@Composable
fun ButtonToTrashcan(onClick: () -> Unit) {
	val context = LocalContext.current
	val viewModel: TrashViewModel = viewModel(
		factory = TrashViewModelFactory(context)
	)
	val enabled by viewModel
		.enabledTrash
		.collectAsState(initial = false)

	IconButton(
		onClick = onClick,
		enabled = enabled,
	) {
		Icon(
			TablerIcons.Trash,
			contentDescription = "Корзина",
			modifier = Modifier.size(28.dp),
		)
	}
}

@Composable
fun ButtonRestoreAll(onClick: () -> Unit) {
	IconButton(
		onClick = onClick,
	) {
		Icon(
			TablerIcons.RotateClockwise,
			contentDescription = "Восстановить всё",
			modifier = Modifier.size(28.dp),
		)
	}
}

@Composable
fun ButtonClearTrashcan(onClick: () -> Unit) {
	IconButton(
		onClick = onClick,
	) {
		Icon(
			TablerIcons.SquareX,
			contentDescription = "Очистить корзину",
			modifier = Modifier.size(28.dp),
		)
	}
}

@Composable
fun ButtonBack(onClick: () -> Unit) {
	IconButton(
		onClick = onClick,
	) {
		Icon(
			TablerIcons.ArrowLeft,
			contentDescription = "Назад",
			modifier = Modifier.size(28.dp),
			tint = MaterialTheme.colorScheme.primary
		)
	}
}