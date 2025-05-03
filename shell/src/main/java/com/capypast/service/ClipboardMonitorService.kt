package com.capypast.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.entities.ClipType
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.utils.copiedClipLabel
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ClipboardMonitorService : AccessibilityService() {
	private val TAG = "ClipboardMonitorService"

	private lateinit var clipboardManager: ClipboardManager
	private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
		Log.d(TAG, "OnPrimaryClipChangedListener: буфер обмена изменился")
		handleClipboardContent()
	}

	private lateinit var database: ClipboardDatabase
	private lateinit var repository: ClipboardRepository

	override fun onCreate() {
		super.onCreate()
		database = ClipboardDatabase.getInstance(this)
		repository = ClipboardRepository(database.clipboardDao())

		clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
		clipboardManager.addPrimaryClipChangedListener(clipChangedListener)

		Log.d(TAG, "Сервис создан, база данных инициализирована, слушатель буфера зарегистрирован")
	}

	override fun onServiceConnected() {
		super.onServiceConnected()
		// Настраиваем фильтрацию событий для AccessibilityService
		val info = AccessibilityServiceInfo().apply {
			// Типы событий, которые хотим получать
			eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
					AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED or
					AccessibilityEvent.TYPE_VIEW_FOCUSED
			// Тип обратной связи (не используется в нашем случае, но необходим для регистрации сервиса)
			feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
			// Дополнительные флаги
			flags = AccessibilityServiceInfo.DEFAULT
		}
		serviceInfo = info  // применяем настройки
		Log.i(TAG, "Сервис подключен (onServiceConnected): события перехвата настроены")
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent) {
		when (event.eventType) {
			AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
				// Проверяем текст события на признак копирования в буфер
				val textList = event.text
				val eventText = textList.joinToString()
				if (eventText.contains("Текст скопирован в буфер обмена.", ignoreCase = true)) {
					// Обнаружен Toast "Copied to clipboard"
					Log.d(TAG, "AccessibilityEvent: обнаружено копирование (Toast 'Copied to clipboard')")
					handleClipboardContent()
				}
			}
			AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
				// Можно использовать как подсказку, что пользователь выделил текст (предположение о будущем копировании)
				Log.d(TAG, "AccessibilityEvent: выделение текста изменилось")
				// (Дополнительная логика при необходимости)
			}
			AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
				// Событие смены фокуса на элемент UI (может быть полезно для контекста)
				Log.d(TAG, "AccessibilityEvent: фокус переключён на другой элемент")
				// (Дополнительная логика при необходимости)
			}
			else -> return
		}
	}

	/**
	 * Обработчик нового содержимого буфера обмена.
	 * Вызывается при срабатывании эвристик (Toast или слушатель буфера).
	 * Считывает ClipData из ClipboardManager, определяет тип (текст/изображение),
	 * и сохраняет в базу данных (с проверкой дубликатов).
	 */
	private fun handleClipboardContent() {
		val clip = clipboardManager.primaryClip ?: return
		if (clip.itemCount < 1) return
		if (isCopyClipHistory(clip)) return

		val item = clip.getItemAt(0)
		var copiedText: String? = null
		var copiedUri: Uri? = null

		when {
			item.text != null -> {
				copiedText = item.text.toString()
				Log.d(TAG, "Скопирован текст: $copiedText")
			}
			item.uri != null -> {
				copiedUri = item.uri
				Log.d(TAG, "Скопирован URI: $copiedUri")
			}
			item.intent != null -> {
				copiedText = item.intent.toUri(0)
				Log.d(TAG, "Скопирован Intent (URI представление): $copiedText")
			}
			else -> {
				Log.w(TAG, "Не удалось определить тип скопированных данных")
				return
			}
		}

		val currentTime = System.currentTimeMillis()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				if (copiedText != null) {
					repository.upsert(
						ClipboardEntity(
							timestamp = currentTime,
							type = ClipType.TEXT,
							content = copiedText,
							tags = "помурчать"
						)
					)
				} else if (copiedUri != null) {
					val lastItem = repository.getLastClip()
					val copiedImg = File(copiedUri.toString())
					var lastItemImg = if (lastItem != null && lastItem.type == ClipType.IMAGE) {
						File(lastItem.content.toString())
					} else {
						null
					}

					if (lastItemImg == null || !imgAreIdentical(copiedImg, lastItemImg)) {
						val path = saveImageAndGetPath(item.uri!!).toString()
						repository.insert(
							ClipboardEntity(
								timestamp = currentTime,
								type = ClipType.IMAGE,
								content = path,
								tags = "поцарапать"
							)
						)
					}
				}
			} catch (e: Exception) {
				Log.e(TAG, "Ошибка при сохранении элемента буфера в базу: ${e.message}", e)
			}
		}
	}

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
			Log.e("ClipService", "Error saving image", e)
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

	override fun onInterrupt() {
		Log.i(TAG, "onInterrupt вызван")
	}

	override fun onDestroy() {
		super.onDestroy()
		clipboardManager.removePrimaryClipChangedListener(clipChangedListener)
		database.close()
		Log.i(TAG, "Сервис уничтожен: слушатели сняты, база данных закрыта")
	}
}