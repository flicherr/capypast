package com.capypast.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

suspend fun saveImage(filename: String, input: InputStream, context: Context): Uri {
	val dir = File(context.filesDir, "images")
	if (!dir.exists()) dir.mkdirs()

	val file = File(dir, filename)
	file.outputStream().use { input.copyTo(it) }

	return Uri.fromFile(file)
}
