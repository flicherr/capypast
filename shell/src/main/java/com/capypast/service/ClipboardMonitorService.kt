package com.capypast.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipboardManager
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.entities.ClipType
import com.capypast.room.repositories.ClipboardRepository
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ClipboardMonitorService : AccessibilityService() {

	private val TAG = "ClipboardMonitorService"

	// Менеджер буфера обмена и слушатель изменений
	private lateinit var clipboardManager: ClipboardManager
	private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
		// Этот метод вызывается при изменении буфера (на Android < 10 или если приложение в фокусе)
		Log.d(TAG, "OnPrimaryClipChangedListener: буфер обмена изменился")
		handleClipboardContent()  // обработка нового содержимого
	}

	// Экземпляр базы данных и DAO для доступа к истории
	private lateinit var database: ClipboardDatabase
	private lateinit var repository: ClipboardRepository

	override fun onCreate() {
		super.onCreate()
		// Инициализация базы данных (Room) один раз при запуске сервиса
		database = ClipboardDatabase.getInstance(this)
		repository = ClipboardRepository(database.clipboardDao())


		// Получаем системный ClipboardManager и регистрируем слушатель изменений
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
				if (eventText.contains("Copied to clipboard", ignoreCase = true)) {
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
		val clip = clipboardManager.primaryClip ?: return  // если буфер пустой или недоступен
		if (clip.itemCount < 1) return

		val item = clip.getItemAt(0)
		// Переменные для извлечённого содержимого
		var copiedText: String? = null
		var copiedUri: Uri? = null

		// Определяем тип содержимого
		when {
			item.text != null -> {
				// Буфер содержит текст (ClipData со строкой)
				copiedText = item.text.toString()
				Log.d(TAG, "Скопирован текст: $copiedText")
			}
			item.uri != null -> {
				// Буфер содержит URI (возможно, изображение или файл)
				copiedUri = item.uri
				Log.d(TAG, "Скопирован URI: $copiedUri")
			}
			item.intent != null -> {
				// Необычный случай: в буфере Intent (например, скопирован в панель Share)
				// Для простоты обрабатываем как текстовое представление Intent
				copiedText = item.intent.toUri(0)
				Log.d(TAG, "Скопирован Intent (URI представление): $copiedText")
			}
			else -> {
				// Неизвестный тип данных
				Log.w(TAG, "Не удалось определить тип скопированных данных")
				return
			}
		}

		// Сохраняем данные в историю через DAO
		val currentTime = System.currentTimeMillis()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				if (copiedText != null) {
					// Проверка дубликата по тексту
					repository.upsert(
						ClipboardEntity(
							timestamp = currentTime,
							type = ClipType.TEXT,
							content = copiedText,
							tags = "помурчать"
						)
					)
					Log.d(TAG, "Добавлена новая текстовая запись в историю")
				} else if (copiedUri != null) {
					val lastItem = repository.getLastClip()
					val copiedImg = File(copiedUri.toString())
					var lastItemImg = if (lastItem != null && lastItem.type == ClipType.IMAGE) {
						File(lastItem.content.toString())
					} else {
						null
					}

					// Првоерка текущего изображения с последним clip'ом истории буфера обмена
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
						Log.d(TAG, "Добавлена новая запись изображения в историю")
					}
				}
			} catch (e: Exception) {
				// Обработка ошибок работы с базой данных или данных буфера
				Log.e(TAG, "Ошибка при сохранении элемента буфера в базу: ${e.message}", e)
			}
		}
	}

	/** Сохраняет URI-картинку в cache-dir и возвращает путь к файлу */
	private fun saveImageAndGetPath(uri: Uri): String? {
		return try {
			val filename = "clip_${System.currentTimeMillis()}.png"
			// кладём во внутренний cache (можно использовать filesDir)
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
		// Метод вызывается при необходимости прервать работу сервиса (не используется в данном контексте)
		Log.i(TAG, "onInterrupt вызван")
	}

	override fun onDestroy() {
		super.onDestroy()
		// Убираем слушатель изменений буфера обмена, чтобы избежать утечки памяти
		clipboardManager.removePrimaryClipChangedListener(clipChangedListener)
		// Закрываем базу данных
		database.close()
		Log.i(TAG, "Сервис уничтожен: слушатели сняты, база данных закрыта")
	}
}