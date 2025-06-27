package com.capypast.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun decodeImage(path: String): Bitmap? {
	var image: Bitmap? = null
	try {
		image = BitmapFactory.decodeFile(path)
	} catch (e: Exception) {
		e.printStackTrace()
	}
	return image
}