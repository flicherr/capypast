package com.capypast.service.overlay

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.capypast.helper.BiometricAuthHelper
import com.capypast.room.entities.ClipType
//import com.capypast.service.MonitorServiceHolder
import com.capypast.ui.overlay.OverlayWindow
import com.capypast.ui.theme.CapypastTheme
import com.capypast.utils.setPrimaryClip
import com.capypast.viewmodel.OverlayViewModel
import com.capypast.viewmodel.factories.OverlayViewModelFactory

class OverlayActivity : FragmentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		window.apply {
			addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

			setLayout(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT
			)

			attributes = attributes.apply {
				gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
				y = 200
			}
		}

		setContent {
			val viewModel = ViewModelProvider(
				ViewModelStore(),
				OverlayViewModelFactory(this@OverlayActivity)
			)[OverlayViewModel::class.java]

			val context = LocalContext.current
			val activity = context as? FragmentActivity ?: return@setContent

			val biometricAuthenticator = remember {
				BiometricAuthHelper(activity, context)
			}

			CapypastTheme {
				OverlayWindow(
					onItemClick = { item ->
						if (!item.isProtected || viewModel.protectedClip == item) {
							setPrimaryClip(
								this@OverlayActivity,
								item.content,
								item.type
							)
							finish()
						} else {
							biometricAuthenticator.authenticate(
								onSuccess = {
									viewModel.readAccess(item)
									setPrimaryClip(
										this@OverlayActivity,
										item.content,
										item.type
									)
								},
								onError = { errorMessage ->
									Toast.makeText(
										this,
										errorMessage,
										Toast.LENGTH_SHORT
									).show()
									viewModel.readAccess(null)
								}
							)
						}
					},
					onDismiss = { finish() },
					viewModel = viewModel
				)
			}
		}
	}

	override fun onDestroy() {
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
		super.onDestroy()
	}
}