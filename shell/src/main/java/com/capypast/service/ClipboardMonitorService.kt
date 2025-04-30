package com.capypast.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipboardManager
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.capypast.room.ClipboardDatabase
import com.capypast.room.ClipboardEntity
import com.capypast.room.ClipType
import com.capypast.room.ClipboardRepository
import kotlinx.coroutines.*

//class ClipboardMonitorrService : Service() {
//
//    companion object {
//        private const val CHANNEL_ID = "clipboard_monitor_channel"
//        private const val NOTIF_ID   = 1001
//    }
//
//    private lateinit var clipboardManager: ClipboardManager
//    private lateinit var listener: ClipboardManager.OnPrimaryClipChangedListener
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//    private val repository: ClipboardRepository by lazy {
//        val dao = ClipboardDatabase.getInstance(applicationContext).clipboardDao()
//        ClipboardRepository(dao)
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//
//        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        listener = ClipboardManager.OnPrimaryClipChangedListener {
//            handleClip()
//        }
//        clipboardManager.addPrimaryClipChangedListener(listener)
//    }
//
//    @SuppressLint("ForegroundServiceType")
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // запуск в foreground, чтобы система не убила сервис
//        startForeground(NOTIF_ID, buildNotification())
//        return START_STICKY
//    }
//
//    private fun handleClip() {
//        val clip = clipboardManager.primaryClip
//        if (clip != null && clip.itemCount > 0) {
//            val item = clip.getItemAt(0)
//
//            var clipType = if (item.text != null ) { ClipType.TEXT } else { ClipType.IMAGE }
//
//            when (clipType) {
//                ClipType.TEXT -> {
//                    val text = item.text.toString()
//                    serviceScope.launch {
//                        repository.insert(
//                            ClipboardEntity(
//                                timestamp = System.currentTimeMillis(),
//                                type = clipType,
//                                content = text,
//                                imagePath = null,
//                                tags = "буковы"
//                            )
//                        )
//                    }
//                }
//                // TODO: можно читать item.uri для картинок
//                ClipType.IMAGE -> {
//                    var pathimg = item.uri.path
//                    println(pathimg)
//                    if (pathimg != null) {
//                        val bitmap = BitmapFactory.decodeFile(pathimg)
//                        println("COPY IMG: $pathimg")
//                        val file = File(filesDir, "clip_${System.currentTimeMillis()}.png")
//                        file.outputStream().use { out ->
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                        }
//
//                        serviceScope.launch {
//                            repository.insert(
//                                ClipboardEntity(
//                                    timestamp = System.currentTimeMillis(),
//                                    type = clipType,
//                                    content = null,
//                                    imagePath = pathimg,
//                                    tags = "кортинка"
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun buildNotification() =
//        NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText("мониторинг буфера обмена…")
//            .setSmallIcon(R.mipmap.ic_main_foreground)
//            .setOngoing(true)
//            .build()
//
//    private fun createNotificationChannel() {
//        val nm = getSystemService(NotificationManager::class.java)
//        nm?.createNotificationChannel(
//            NotificationChannel(
//                CHANNEL_ID,
//                "мониторинг буфера обмена",
//                NotificationManager.IMPORTANCE_LOW
//            )
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        clipboardManager.removePrimaryClipChangedListener(listener)
//        serviceScope.cancel()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}


//class ClipboardMonitorService : AccessibilityService() {
//    private lateinit var clipboardManager: ClipboardManager
//    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//        private val repository: ClipboardRepository by lazy {
//        val dao = ClipboardDatabase.getInstance(applicationContext).clipboardDao()
//        ClipboardRepository(dao)
//    }
////    private var lastText: String? = null
////    private var lastImg: Uri? = null
//
//    override fun onServiceConnected() {
//        super.onServiceConnected()
//        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//    }
//
//    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        Log.d("CLIP_SERVICE", "Event type=${event.eventType}")
//        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
//            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
//            event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
//
//            handleClip()
//        }
//    }
//
//    private fun handleClip() {
//        val clip = clipboardManager.primaryClip ?: return
//        if (clip.itemCount == 0) return
//
//        val item = clip.getItemAt(0)
//        if (item.uri == null && item.text == null) return
//        var clipType = if (item.text != null ) { ClipType.TEXT } else { ClipType.IMAGE }
////        if (item.text.toString() == lastText) return
////        if (item.uri!! == lastImg) return
//
//        var entity: ClipboardEntity
//        when (clipType) {
//            ClipType.TEXT -> {
////                lastText = item.text.toString()
//                entity = ClipboardEntity(
//                    timestamp = System.currentTimeMillis(),
//                    type = ClipType.TEXT,
//                    content = item.text.toString(),
//                    imagePath = null,
//                    tags = "буковы"
//                )
//            }
//            ClipType.IMAGE -> {
////                lastImg = item.uri
//                val path = saveImageAndGetPath(item.uri!!)
//                entity = ClipboardEntity(
//                    timestamp = System.currentTimeMillis(),
//                    type      = ClipType.IMAGE,
//                    content   = null,
//                    imagePath = path,
//                    tags      = "кортинка"
//                )
//            }
//        }
//        serviceScope.launch {
//            repository.insert(entity)
//        }
//    }
//
//    private fun saveImageAndGetPath(uri: Uri): String? {
//        return try {
//            // генерируем уникальное имя
//            val filename = "clip_${System.currentTimeMillis()}.png"
//            // кладём во внутренний cache (можно использовать filesDir)
//            val file = File(cacheDir, filename)
//            contentResolver.openInputStream(uri)?.use { input ->
//                FileOutputStream(file).use { output ->
//                    input.copyTo(output)
//                }
//            }
//            file.absolutePath
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    override fun onInterrupt() {
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        serviceScope.cancel()
//    }
//}


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
                val textList = event.text ?: return
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
                    val existingItem = repository.findByText(copiedText.toString())
                    if (existingItem != null) {
                        // Дубликат найден: обновляем время и записываем изменения
                        repository.updateTimestamp(existingItem, currentTime)
                        Log.d(TAG, "Обновлена отметка времени для существующей текстовой записи")
                    } else {
                        // Новая текстовая запись
                        repository.insert(
                            ClipboardEntity(
                                timestamp = currentTime,
                                type = ClipType.TEXT,
                                content = copiedText,
                                imagePath = null,
                                tags = ""
                            )
                        )
                        Log.d(TAG, "Добавлена новая текстовая запись в историю")
                    }
                } else if (copiedUri != null) {
                    // Представляем URI как строку для хранения. Можно также сохранить изображение побайтово.
                    val uriString = copiedUri.toString()
                    val existingItem = repository.findByImage(uriString)
                    if (existingItem != null) {
                        // Дубликат изображения найден
                        repository.updateTimestamp(existingItem, currentTime)
                        Log.d(TAG, "Обновлена отметка времени для существующей записи изображения")
                    } else {
                        // Новая запись с URI изображения
                        repository.insert(
                            ClipboardEntity(
                                timestamp = currentTime,
                                type = ClipType.IMAGE,
                                content = null,
                                imagePath = copiedUri.toString(),
                                tags = ""
                            )
                        )
                        Log.d(TAG, "Добавлена новая запись изображения в историю")
                        // (*) Опционально: можно здесь же выполнить сохранение самого изображения во внутреннее хранилище
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок работы с базой данных или данных буфера
                Log.e(TAG, "Ошибка при сохранении элемента буфера в базу: ${e.message}", e)
            }
        }
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