package com.capypast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capypast.ui.theme.ThemedBackground
import com.capypast.ui.screens.history.History
import com.capypast.ui.screens.settings.Settings
import com.capypast.ui.screens.trashcan.Trashcan
import com.capypast.ui.theme.CapypastTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val navCtrl = rememberNavController()

			CapypastTheme {
				ThemedBackground()

				NavHost(
					navController = navCtrl,
					startDestination = "main",
				) {
					composable("main") {
						History { navCtrl.navigate("settings") }
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