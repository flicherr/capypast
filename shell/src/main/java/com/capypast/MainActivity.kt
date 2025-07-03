package com.capypast

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capypast.ui.theme.ThemedBackground
import com.capypast.ui.screens.clipboard.Clipboard
import com.capypast.ui.screens.settings.Settings
import com.capypast.ui.screens.trashcan.Trashcan
import com.capypast.ui.theme.CapypastTheme
import android.util.Log
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity  () {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		Log.d("MainActivity", "Current process: ${android.os.Process.myPid()}")

		setContent {
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
						Trashcan(
							onClickToBack = { navCtrl.popBackStack() }
						)
					}
				}
			}
		}
	}
}