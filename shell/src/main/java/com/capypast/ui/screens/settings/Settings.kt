package com.capypast.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.capypast.room.interactors.ExportBackupInteractor
import com.capypast.room.interactors.ImportBackupInteractor
import compose.icons.TablerIcons
import compose.icons.tablericons.DatabaseExport
import compose.icons.tablericons.DatabaseImport
import compose.icons.tablericons.Settings
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Settings(
	onClickToTrashcan: () -> Unit,
	onClickToBack: () -> Unit,
) {
	val snackbarHostState = remember { SnackbarHostState() }

	Scaffold(
		topBar = {
			AppBar(
				title = "настройки",
				onClickToTrashcan = onClickToTrashcan,
				onClickToBack = onClickToBack
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		containerColor = MaterialTheme.colorScheme.surface
	) { padding ->
		Surface(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize(),
		) {
			Column {
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
						title = "управление мониторингом CP",
						icon = TablerIcons.Settings
					)
					BackupControls(snackbarHostState)
				}
			}
		}
	}
}

fun clickToOnMonitoring(context: Context) {
	val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
		addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}
	context.startActivity(intent)

	// -------- разрешение на оверлей ---------
	if (!Settings.canDrawOverlays(context)) {
		val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
			("package:" + context.packageName).toUri())
		context.startActivity(intent)
	}
}

suspend fun exportBackup(context: Context, uri: Uri) {
	val export = GlobalContext.get().get<ExportBackupInteractor>()
	export(context, uri)
}

suspend fun importBackup(context: Context, uri: Uri) {
	val import =  GlobalContext.get().get<ImportBackupInteractor>()
	import(context, uri)
}

@Composable
fun BackupControls(snackbarHostState: SnackbarHostState) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val exportLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.CreateDocument("application/zip")
	) { uri ->
		if (uri != null) {
			scope.launch {
				val result = runCatching {
					exportBackup(context, uri)
				}
				snackbarHostState
					.showSnackbar(
						message = "Резервная копия успешно сохранена"
							.takeIf { result.isSuccess }
							?: ("Ошибка создания резервной копии данных: " +
									"${result.exceptionOrNull()?.message}"),
						withDismissAction = true,
					)
			}
		}
	}

	val importLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument()
	) { uri ->
		if (uri != null) {
			scope.launch {
				val result = runCatching {
					importBackup(context, uri)
				}
				snackbarHostState.showSnackbar(
					message = "Восстановление данных успешно завершено"
						.takeIf { result.isSuccess }
						?: ("Ошибка восстановления данных: " +
								"${result.exceptionOrNull()?.message}"),
					withDismissAction = true,
				)
			}
		}
	}

	ActionButton(
		onClick = {
			val date = SimpleDateFormat(
				"yyyy-MM-dd_HH-mm",
				Locale.getDefault()
			).format(Date())
			exportLauncher.launch("backup_$date.zip")
		},
		title = "сохранить резервную копию",
		icon = TablerIcons.DatabaseExport
	)

	ActionButton(
		onClick = {
			importLauncher.launch(arrayOf("application/zip"))
		},
		title = "восстановить данные из файла",
		icon = TablerIcons.DatabaseImport
	)
}