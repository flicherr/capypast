package com.capypast.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.capypast.service.clipboard.ForegroundActivity
import com.capypast.service.overlay.OverlayActivity

class MonitorService : AccessibilityService() {
	private var TAG = "MonitorService"

	/**
	 * Мониторинг тройного тапа по полю ввода в сторонних приложениях
	 * для вызова оверлея с историей буфера обмена
	 */
	val tapTimestamps = mutableListOf<Long>()

	/**
	 * Мониторинг пользовательских действий копирования фрагментов данных
	 * для обработки и локального сохранения в истории буфера обмена приложения
	 */
	private var lastSelection: String? = null
	private var lastSelectedRange: Pair<Int, Int>? = null
	private var lastFocusedViewId: Int = -1

	private val copyWords = listOf(
		"copy", "copied", "копировать", "копирован", "копи", "скопировать",
		"copy text", "copy link", "копировать ссылку", "share", "поделиться",
		"ссылка", "ссылку", "link", "вырезать", "cut", "в буфер обмена",
		"копировать url", "url", "коп."
	)

	override fun onCreate() {
		super.onCreate()
		Log.d(TAG, "Current process: ${android.os.Process.myPid()}")
	}

	override fun onDestroy() {
		Log.w(TAG, "Сервис уничтожен")
		super.onDestroy()
	}

	override fun onInterrupt() {
		Log.i(TAG, "onInterrupt вызван")
	}

	override fun onUnbind(intent: Intent?): Boolean {
		Log.w(TAG, "Сервис отвязан")
		return super.onUnbind(intent)
	}

	override fun onServiceConnected() {
		super.onServiceConnected()

		// Настраиваем фильтрацию событий для AccessibilityService
		val info = AccessibilityServiceInfo().apply {
			// Типы событий, которые хотим получать
			eventTypes = AccessibilityEvent.TYPES_ALL_MASK
			// Тип обратной связи (не используется в нашем случае, но необходим для регистрации сервиса)
			feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
			// Дополнительные флаги
			flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
					AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
		}
		serviceInfo = info

		Log.i(TAG, "Сервис подключен (onServiceConnected): события перехвата настроены")
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent) {
		try {
			detectCopy(event)
			detectCallOverlay(event)
		} catch (e: Exception) {
			Log.i(TAG, "${e.localizedMessage}")
		}
	}

	private fun detectCallOverlay(event: AccessibilityEvent) {
		if (event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED &&
			event.eventType != AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END
		) return

		val now = System.currentTimeMillis()
		tapTimestamps.add(now)

		tapTimestamps.removeAll { now - it > 500 }

		if (tapTimestamps.size == 3) {
			Log.d("TapDetect", "Тройной тап")
			tapTimestamps.clear()
			callOverlay()
		}
	}

//	fun pasteText(text: String) {
//		val root = rootInActiveWindow ?: return
//		val focus = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
//		if (focus != null && focus.isEditable) {
//			val args = Bundle().apply {
//				putCharSequence(
//					AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
//					text
//				)
//			}
//			focus.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
//		}
//	}

	private fun detectCopy(event: AccessibilityEvent) {
		when (event.eventType) {
			AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
				val text = event.text.firstOrNull()
				text?.let {
					val pkgName = (event.packageName ?: "unknown").toString()

					val from = event.fromIndex
					val to = event.toIndex

					if (from != to && from >= 0 && to >= 0) {
						val selected = text.substring(
							minOf(from, to),
							maxOf(from, to)
						)
						if (selected.isNotBlank()) {
							if (lastSelectedRange != Pair(from, to)
								&& lastSelection != selected
							) {
								lastSelection = selected
								lastSelectedRange = Pair(from, to)
								lastFocusedViewId =
									event.source?.viewIdResourceName?.hashCode() ?: -1
								Log.d("SelectionTracker", "Выделен текст: $selected в $pkgName")
							}
						}
					}
				}
			}

			AccessibilityEvent.TYPE_VIEW_CLICKED,
			AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED,
			AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
			AccessibilityEvent.TYPE_ANNOUNCEMENT,
			AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
				val eventText = event.text.joinToString().lowercase()
				val pkgName = (event.packageName ?: "unknown").toString()
				val viewId = event.source?.viewIdResourceName?.hashCode() ?: -1

				if (isClickCopy(eventText)) {
					Log.d("CopyDetect", "Подозрение на копирование: '$eventText'")
					heuristicClipSave(pkgName)
				} else if (viewId == lastFocusedViewId && !lastSelection.isNullOrBlank()) {
					Log.d(
						"CopyDetect",
						"Попытка копирования после выделения текста: '$lastSelection'"
					)
					heuristicClipSave(pkgName, true)
				}
			}

			else -> return
		}
	}

	private fun callOverlay() {
		try {
			if (!Settings.canDrawOverlays(this)) {
				Toast
					.makeText(
						applicationContext,
						"Нет разрешения на отображение поверх других окон",
						Toast.LENGTH_SHORT
					)
					.show()
				return
			}
			Intent(this, OverlayActivity::class.java).apply {
				addFlags(
					Intent.FLAG_ACTIVITY_NEW_TASK or
							Intent.FLAG_ACTIVITY_CLEAR_TOP or
							Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
				)
				startActivity(this)
			}
		} catch (e: Exception) {
			Log.i(TAG, "${e.localizedMessage}")
		}
	}

	private fun isClickCopy(text: String): Boolean {
		return copyWords.any { text.contains(it, ignoreCase = true) }
	}

	/**
	 * Эвристика: считаем, что пользователь скопировал данные,
	 * если оно попало в буфер в течение 10 секунды после нажатия на кнопку
	 */
	private fun heuristicClipSave(pkg: String, expectedText: Boolean = false) {
		val intent = Intent(this, ForegroundActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
			putExtra("source_package", pkg)
			if (expectedText) {
				putExtra("expected_text", lastSelection)
				lastSelection = null
				lastSelectedRange = null
			}
		}
		startActivity(intent)
	}
}