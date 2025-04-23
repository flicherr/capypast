package com.capypast

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capypast.room.ClipboardDatabase
import com.capypast.room.ClipboardRepository
import com.capypast.service.ClipboardMonitorService
import com.capypast.ui.fragments.ThemedBackground
import com.capypast.ui.screens.Main
import com.capypast.ui.screens.Settings
import com.capypast.ui.screens.Trashcan
import com.capypast.ui.theme.CapypastTheme
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.ClipboardViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val svcIntent = Intent(this, ClipboardMonitorService::class.java)
        startForegroundService(svcIntent)

        val db = ClipboardDatabase.Companion.getInstance(applicationContext)
        val dao = db.clipboardDao()
        val repository = ClipboardRepository(dao)
        val factory = ClipboardViewModelFactory(repository)
        val vm = ViewModelProvider(this, factory)[ClipboardViewModel::class.java]

        lifecycleScope.launch {
//            vm.insert(
//                ClipboardEntity(
//                    timestamp = System.currentTimeMillis(),
//                    type = ClipType.TEXT,
//                    content = "тюленька, [16.04.2025 23:41]\n" +
//                            "Сфоткай стол где ноутбук\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "Ничего не убирая.\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "Даю 5 сек\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "1\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "2\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "3\n" +
//                            "\n" +
//                            "тюленька, [16.04.2025 23:41]\n" +
//                            "4",
//                    imagePath = null,
//                    tags = "фрфрфр"
//                )
//            )

//             — картинка из ресурсов (res/drawable/test_image.png)
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
//
//            val file = File(filesDir, "clip_${System.currentTimeMillis()}.png")
//            file.outputStream().use { out ->
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//            }
//
//            vm.insert(
//                ClipboardEntity(
//                    timestamp = System.currentTimeMillis(),
//                    type = ClipType.IMAGE,
//                    content = null,
//                    imagePath = file.absolutePath,
//                    tags = "мулька"
//                )
//            )
        }

        setContent {
            val navCtrl = rememberNavController()

            CapypastTheme {
                ThemedBackground()

                NavHost(
                    navController = navCtrl,
                    startDestination = "main",
                ) {
                    composable("main") {
                        Main { navCtrl.navigate("settings") }
                    }
                    composable("settings") {
                        Settings(
                            onClickToTrashcan = { navCtrl.navigate("trashcan") },
                            onClickToBack = { navCtrl.popBackStack() },
                        )
                    }
                    composable("trashcan") {
                        Trashcan {
                            navCtrl.popBackStack()
                        }
                    }
                }
            }
        }
    }
}