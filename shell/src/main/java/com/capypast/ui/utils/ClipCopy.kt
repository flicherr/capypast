package com.capypast.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.widget.Toast
import com.capypast.room.entities.ClipType
import java.io.File

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
						"ыаъъыуаъуыъ",
						content
					)
			}

			ClipType.IMAGE -> {
				ClipData
					.newUri(
						context.contentResolver,
						"ъиооъиъиоъи",
						Uri.fromFile(
							File(content)
						)
					)
			}
		}
	)
	Toast
		.makeText(
			context,
			"Содержимое скопировано!",
			Toast.LENGTH_SHORT
		)
		.show()
}