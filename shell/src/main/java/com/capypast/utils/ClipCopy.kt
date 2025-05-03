package com.capypast.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.core.net.toUri
import com.capypast.room.entities.ClipType

const val copiedClipLabel = "ыаъъыуаъуыъ"

fun clipCopy(
	context: Context,
	content: String,
	type: ClipType
) {
	val clipboardManager = context
		.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

	clipboardManager.setPrimaryClip(
		when (type) {
			ClipType.TEXT -> {
				ClipData
					.newPlainText(
						copiedClipLabel,
						content
					)
			}
			ClipType.IMAGE -> {
				ClipData
					.newUri(
						context.contentResolver,
						copiedClipLabel,
						content.toUri()
					)
			}
		}
	)
}