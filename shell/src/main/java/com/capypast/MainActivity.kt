package com.capypast

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capypast.service.OverlayService
import com.capypast.ui.theme.ThemedBackground
import com.capypast.ui.screens.clipboard.Clipboard
import com.capypast.ui.screens.settings.Settings
import com.capypast.ui.screens.trashcan.Trashcan
import com.capypast.ui.theme.CapypastTheme
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.FragmentActivity


class MainActivity : FragmentActivity  () {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		Log.d("MainActivity", "Current process: ${android.os.Process.myPid()}")

		setContent {
			// -------- разрешение на оверлей ---------
//			val context = LocalContext.current
//			if (!Settings.canDrawOverlays(context)) {
//				val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//					("package:" + context.packageName).toUri())
//				startActivity(intent)
//			}

			val navCtrl = rememberNavController()

			CapypastTheme {
				ThemedBackground()

				NavHost(
					navController = navCtrl,
					startDestination = "clipboard",
				) {
					composable("clipboard") {
						Clipboard { navCtrl.navigate("settings") }
					}
					composable("settings") {
						Settings(
							onClickToTrashcan = { navCtrl.navigate("trashcan") },
							onClickToBack = { navCtrl.popBackStack() },
						)
					}
					composable("trashcan") {
						Trashcan (
							onClickToBack = { navCtrl.popBackStack() }
						)
					}
				}
			}
		}
	}
}