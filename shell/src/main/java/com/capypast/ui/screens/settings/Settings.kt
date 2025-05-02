package com.capypast.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.capypast.room.ClipboardDatabase
import com.capypast.room.repositories.ClipboardRepository
import compose.icons.TablerIcons
import compose.icons.tablericons.Settings
import compose.icons.tablericons.SquareX
import compose.icons.tablericons.Upload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
	onClickToTrashcan: () -> Unit,
	onClickToBack: () -> Unit,
) {
	Surface(
		modifier = Modifier.fillMaxSize(),
	) {
		Column {
			AppBar(
				title = "настройки",
				onClickToTrashcan = onClickToTrashcan,
				onClickToBack = onClickToBack
			)

			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
			)

			Column(
				modifier = Modifier.padding(horizontal = 10.dp)
			) {
				val context = LocalContext.current
				ActionButton(
					onClick = { clickToOnMonitoring(context)},
					title = "включить мониторинг",
					icon = TablerIcons.Settings
				)
				ActionButton(
					onClick = { clickToCleanHistory(context) },
					title = "очистить историю",
					icon = TablerIcons.SquareX
				)
				ActionButton(
					onClick = { clickToExportHistory() },
					title = "экспорт истории",
					icon = TablerIcons.Upload
				)
			}
		}
	}
}

@SuppressLint("CoroutineCreationDuringComposition")
fun clickToCleanHistory(context: Context) {
	var repo = ClipboardRepository(
		ClipboardDatabase.getInstance(context)
			.clipboardDao()
	)
	CoroutineScope(Dispatchers.IO).launch {
		repo.clear()
	}
}

fun clickToOnMonitoring(context: Context) {
	val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
		addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
	context.startActivity(intent)
}

fun clickToExportHistory() {
	/** мяу мяу */
}