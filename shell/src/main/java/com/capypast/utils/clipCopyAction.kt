package com.capypast.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.capypast.room.entities.ClipType
import java.io.File

const val copiedClipLabel = "ыаъъыуаъуыъ"

fun setPrimaryClip(
	context: Context,
	content: String,
	type: ClipType
) {
	val clipboardManager = context
		.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
	when (type) {
		ClipType.TEXT -> {
			clipboardManager.setPrimaryClip(
				ClipData
					.newPlainText(
						copiedClipLabel,
						content
					)
			)

		}

		ClipType.IMAGE -> {
			val imageFile = File(content)
			if (!imageFile.exists()) {
				Log.w("Clipboard", "Файл не найден: $content")
				return
			}
			val uri = FileProvider.getUriForFile(
				context,
				"${context.packageName}.fileprovider",
				imageFile
			)
			clipboardManager.setPrimaryClip(
				ClipData.newUri(
					context.contentResolver,
					"Image", uri
				)
			)
			context.grantUriPermission(
				context.packageManager.resolveActivity(
					Intent(Intent.ACTION_MAIN),
					PackageManager.MATCH_DEFAULT_ONLY
				)?.activityInfo?.packageName ?: "unknown",
				uri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION
			)
		}
	}
}