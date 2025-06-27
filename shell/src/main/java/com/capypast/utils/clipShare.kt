package com.capypast.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.ClipType
import java.io.File

fun clipShare(context: Context, item: ClipEntity) {
	when (item.type) {
		ClipType.TEXT -> {
			val shareIntent = Intent(Intent.ACTION_SEND).apply {
				type = "text/plain"
				putExtra(Intent.EXTRA_TEXT, item.content)
			}
			context.startActivity(Intent.createChooser(shareIntent, "Поделиться..."))
		}

		ClipType.IMAGE -> {
			val file = File(context.filesDir, "${item.content.toUri().lastPathSegment}")
			val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
			Log.i("ClipShare", uri.toString())
			val shareIntent = Intent(Intent.ACTION_SEND).apply {
				type = "image/png"
				putExtra(Intent.EXTRA_STREAM, uri)
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			}

			// Разрешение временного доступа, если используется FileProvider
			context.grantUriPermission(
				context.packageManager.resolveActivity(
					shareIntent,
					PackageManager.MATCH_DEFAULT_ONLY
				)?.activityInfo?.packageName ?: "",
				uri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION
			)

			context.startActivity(Intent.createChooser(shareIntent, "Поделиться..."))
		}
	}
}
