package com.capypast.service.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.ClipType
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.ui.theme.TransparentTheme
import com.capypast.utils.copiedClipLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ForegroundActivity : ComponentActivity() {

	private lateinit var database: ClipboardDatabase
	private lateinit var repository: ClipboardRepository

	private var TAG: String = "ClipboardActivity"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		database = ClipboardDatabase.Companion.getInstance(this)
		repository = ClipboardRepository(database.clipDao())

		window.apply {
			clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

			setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				2
			)
			attributes = attributes.apply {
				gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
				y = 100
				dimAmount = 0f
			}
		}

		setContent {
			TransparentTheme()
		}

		lifecycleScope.launch {
			delay(89)
			readClipboard()
			finish()
		}
	}

	override fun onDestroy() {
		window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
		super.onDestroy()
	}

	private suspend fun readClipboard() = withContext(Dispatchers.IO) {
		val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
		val clip = clipboard.primaryClip ?: return@withContext

		if (clip.itemCount == 0) return@withContext
		if (isCopyClipHistory(clip)) return@withContext

		val item = clip.getItemAt(0)
		val timeNow = System.currentTimeMillis()
		try {
			item.text?.let { text ->
				intent.getStringExtra("expected_text")?.let { expectedText ->
					if (expectedText.equals(text.toString())) {
						Log.d(TAG, "Текст успешно скопирован: $text")
						saveTextClip(text.toString(), timeNow)
					} else {
						Log.d(
							TAG,
							"Буфер: '$text' не соответствует ожидаемому тексту: '$expectedText'."
						)
						return@withContext
					}
				}

				Log.d(TAG, "Скопирован текст: $text")
				saveTextClip(text.toString(), timeNow)
			}
			item.uri?.let { uri ->
				if (listOf("png", "jpg", "jpeg", "gif", "bmp", "img", "webp").any {
						uri.toString().substringAfterLast('.').lowercase()
							.contains(it, ignoreCase = true)
					}
				) {

					Log.d(TAG, "Скопирован URI: $uri")
					saveImageClip(uri, timeNow)
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "Ошибка при сохранении элемента буфера в базу: ${e.message}", e)
		}
	}

	private suspend fun saveTextClip(text: String, timeNow: Long) =
		withContext(Dispatchers.IO) {
			repository.upsert(
				ClipEntity(
					timestamp = timeNow,
					type = ClipType.TEXT,
					content = text,
					tags = pkgName()
				)
			)
		}

	private suspend fun saveImageClip(uri: Uri, timeNow: Long) =
		withContext(Dispatchers.IO) {
			val lastItem = repository.lastClip()
			val copiedImg = File(uri.toString())
			var lastItemImg = if (lastItem != null && lastItem.type == ClipType.IMAGE) {
				File(lastItem.content.toString())
			} else {
				null
			}

			if (lastItemImg == null || !imgAreIdentical(copiedImg, lastItemImg)) {
				val path = saveImageAndGetPath(uri).toString()
				repository.insert(
					ClipEntity(
						timestamp = timeNow,
						type = ClipType.IMAGE,
						content = path,
						tags = pkgName()
					)
				)
			}
		}

	private fun pkgName() : String = intent
		.getStringExtra("source_package") ?: "unknown"

	private fun isCopyClipHistory(clip: ClipData): Boolean =
		clip.description.label == copiedClipLabel

	private fun saveImageAndGetPath(uri: Uri): String? {
		return try {
			val filename = "clip_${System.currentTimeMillis()}.png"
			val file = File(filesDir, filename)

			contentResolver.openInputStream(uri)?.use { input ->
				FileOutputStream(file).use { output ->
					input.copyTo(output)
				}
			}

			file.absolutePath
		} catch (e: Exception) {
			Log.e("contentResolver", "Ошибка сохранения изображения", e)
			null
		}
	}

	fun File.sha256(): String {
		val bytes = MessageDigest
			.getInstance("SHA-256")
			.digest(this.readBytes())
		return bytes.joinToString("") { "%02x".format(it) }
	}

	fun imgAreIdentical(file1: File, file2: File): Boolean {
		return file1.sha256() == file2.sha256()
	}
}