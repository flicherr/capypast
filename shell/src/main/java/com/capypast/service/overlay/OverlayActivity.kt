package com.capypast.service.overlay

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.capypast.helper.BiometricAuthHelper
import com.capypast.ui.overlay.OverlayWindow
import com.capypast.ui.theme.CapypastTheme
import com.capypast.utils.setPrimaryClip
import com.capypast.viewmodel.OverlayViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

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
			val viewModel: OverlayViewModel by viewModel()

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