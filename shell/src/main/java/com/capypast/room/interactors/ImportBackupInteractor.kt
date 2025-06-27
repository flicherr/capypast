package com.capypast.room.interactors

import android.content.Context
import android.net.Uri
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.BackupData
import com.capypast.room.entities.ClipType
import com.capypast.utils.saveImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipInputStream

class ImportBackupInteractor(
	private val db: ClipboardDatabase
) {
	suspend operator fun invoke(context: Context, importPath: Uri) {
		val resolver = context.contentResolver
		val zipInput = ZipInputStream(
			resolver.openInputStream(importPath)
				?: error("Can't open input stream")
		)
		val entities = mutableMapOf<String, ByteArray>()

		var entity = zipInput.nextEntry
		while (entity != null) {
			val bytes = zipInput.readBytes()
			entities[entity.name] = bytes
			zipInput.closeEntry()
			entity = zipInput.nextEntry
		}
		zipInput.close()

		val json = entities["data.json"]?.toString(Charsets.UTF_8) ?: error("No data.json in archive")
		val gson = Gson()
		val type = object : TypeToken<BackupData>() {}.type
		val data: BackupData = gson.fromJson(json, type)

		val updatedClipboard = data.clipboard.map { item ->
			if (item.type == ClipType.IMAGE && item.content.startsWith("images/")) {
				val bytes = entities[item.content] ?: error("Missing image ${item.content}")
				val filename = item.content.substringAfterLast("/")
				val savedUri = saveImage(filename, ByteArrayInputStream(bytes), context)
				File(savedUri.path!!).absolutePath.let { absPath ->
					item.copy(content = absPath)
				}
			} else item
		}

		val updatedTrashcan = data.trashcan.map { item ->
			if (item.type == ClipType.IMAGE && item.content.startsWith("images/")) {
				val bytes = entities[item.content] ?: error("Missing image ${item.content}")
				val filename = item.content.substringAfterLast("/")
				val savedUri = saveImage(filename, ByteArrayInputStream(bytes), context)
				File(savedUri.path!!).absolutePath.let { absPath ->
					item.copy(content = absPath)
				}
			} else item
		}

		db.clipDao().insertSome(updatedClipboard)
		db.trashDao().insertSome(updatedTrashcan)
	}
}