package com.capypast.room.interactors

import android.content.Context
import android.net.Uri
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.BackupData
import com.capypast.room.entities.ClipType
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportBackupInteractor(
	private val db: ClipboardDatabase
) {
	suspend operator fun invoke(context: Context, exportPath: Uri) {
		val gson = Gson()
		val resolver = context.contentResolver
		val zipStream = ZipOutputStream(
			resolver.openOutputStream(exportPath)
				?: error("Can't open output stream")
		)

		val addedImages = mutableSetOf<String>()
		try {
			val clipboard = db.clipDao().allItems().map { item ->
				if (item.type == ClipType.IMAGE) {
					val originalFile = File(item.content)
					val filename = "images/${originalFile.name}"

					if (!addedImages.contains(filename)) {
						if (originalFile.exists()) {
							zipStream.putNextEntry(ZipEntry(filename))
							FileInputStream(originalFile).use { it.copyTo(zipStream) }
							zipStream.closeEntry()
							addedImages.add(filename)
						}
					}
					item.copy(content = filename)
				} else {
					item
				}
			}

			val trashcan = db.trashDao().allItems().map { item ->
				if (item.type == ClipType.IMAGE) {
					val originalFile = File(item.content)
					val filename = "images/${originalFile.name}"
					if (!addedImages.contains(filename)) {
						if (originalFile.exists()) {
							zipStream.putNextEntry(ZipEntry(filename))
							FileInputStream(originalFile).use { it.copyTo(zipStream) }
							zipStream.closeEntry()
							addedImages.add(filename)
						}
					}
					item.copy(content = filename)
				} else {
					item
				}
			}

			val json = gson.toJson(BackupData(clipboard, trashcan))
			zipStream.putNextEntry(ZipEntry("data.json"))
			zipStream.write(json.toByteArray())
			zipStream.closeEntry()

		} finally {
			zipStream.close()
		}
	}
}