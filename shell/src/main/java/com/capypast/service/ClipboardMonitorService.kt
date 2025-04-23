package com.capypast.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.capypast.R
import com.capypast.room.ClipboardDatabase
import com.capypast.room.ClipboardEntity
import com.capypast.room.ClipType
import com.capypast.room.ClipboardRepository
import kotlinx.coroutines.*
import java.io.File

class ClipboardMonitorService : Service() {

    companion object {
        private const val CHANNEL_ID = "clipboard_monitor_channel"
        private const val NOTIF_ID   = 1001
    }

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var listener: ClipboardManager.OnPrimaryClipChangedListener
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository: ClipboardRepository by lazy {
        val dao = ClipboardDatabase.getInstance(applicationContext).clipboardDao()
        ClipboardRepository(dao)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()


        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        listener = ClipboardManager.OnPrimaryClipChangedListener {
            handleClip()
        }
        clipboardManager.addPrimaryClipChangedListener(listener)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // запуск в foreground, чтобы система не убила сервис
        startForeground(NOTIF_ID, buildNotification())
        return START_STICKY
    }

    private fun handleClip() {
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val item = clip.getItemAt(0)

            var clipType = if (item.text != null ) { ClipType.TEXT } else { ClipType.IMAGE }

            when (clipType) {
                ClipType.TEXT -> {
                    val text = item.text.toString()
                    serviceScope.launch {
                        repository.insert(
                            ClipboardEntity(
                                timestamp = System.currentTimeMillis(),
                                type = clipType,
                                content = text,
                                imagePath = null,
                                tags = "буковы"
                            )
                        )
                    }
                }
                // TODO: можно читать item.uri для картинок
                ClipType.IMAGE -> {
                    var pathimg = item.uri.path
                    println(pathimg)
                    if (pathimg != null) {
                        val bitmap = BitmapFactory.decodeFile(pathimg)
                        println("COPY IMG: $pathimg")
                        val file = File(filesDir, "clip_${System.currentTimeMillis()}.png")
                        file.outputStream().use { out ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }

                        serviceScope.launch {
                            repository.insert(
                                ClipboardEntity(
                                    timestamp = System.currentTimeMillis(),
                                    type = clipType,
                                    content = null,
                                    imagePath = pathimg,
                                    tags = "кортинка"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("мониторинг буфера обмена…")
            .setSmallIcon(R.mipmap.ic_main_foreground)
            .setOngoing(true)
            .build()

    private fun createNotificationChannel() {
        val nm = getSystemService(NotificationManager::class.java)
        nm?.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "мониторинг буфера обмена",
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardManager.removePrimaryClipChangedListener(listener)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
