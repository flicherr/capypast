package com.capypast.service

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.capypast.ui.overlay.OverlayWindow
import com.capypast.utils.setPrimaryClip
import com.capypast.viewmodel.OverlayViewModel
import com.capypast.viewmodel.factories.OverlayViewModelFactory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import com.capypast.service.overlay.FakeLifecycleOwner
import com.capypast.ui.theme.TransparentTheme

//class OverlayService() : Service() {
//	private lateinit var fakeOwner: FakeLifecycleOwner
//
//	private lateinit var windowManager: WindowManager
//	private lateinit var overlayView: ComposeView
//
//	override fun onBind(intent: Intent?): IBinder? = null
//
//	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//		showOverlay()
//		return START_STICKY
//	}
//
//	override fun onDestroy() {
//		if (::overlayView.isInitialized) {
//			windowManager.removeView(overlayView)
//		}
////		if (::lifecycle.isInitialized) {
////			lifecycle.destroy()
////		}
//		if (::fakeOwner.isInitialized) {
//			fakeOwner.destroy()
//		}
//		super.onDestroy()
//	}
//
//	@SuppressLint("ClickableViewAccessibility")
//	private fun showOverlay() {
//		if (::overlayView.isInitialized) return
//
//		val composeView = ComposeView(this)
//
//		fakeOwner = FakeLifecycleOwner()
//		fakeOwner.initialize() // performAttach -> lifecycle = CREATED
//		fakeOwner.attachToView(composeView)
//		fakeOwner.moveToStarted()
//		fakeOwner.moveToResumed()
//
//		composeView.setContent {
////			val viewModel = ViewModelProvider(
////				ViewModelStore(),
////				OverlayViewModelFactory(this@OverlayService)
////			)[OverlayViewModel::class.java]
//
//			OverlayWindow(
//				onItemClick = { item ->
////					viewModel.select(item)
//					setPrimaryClip(
//						this@OverlayService,
//						item.content,
//						item.type
//					)
//					hideOverlay()
//				},
//				onDismiss = {
//					hideOverlay()
//				},
////				viewModel = viewModel
//			)
//		}
//
//		composeView.setBackgroundColor(Color.TRANSPARENT)
//		composeView.isFocusable = true
//
//		overlayView = composeView
//		addOverlay()
//	}
//
//	private fun addOverlay() {
//		val params = WindowManager.LayoutParams(
//			WindowManager.LayoutParams.MATCH_PARENT,
//			WindowManager.LayoutParams.MATCH_PARENT,
//			WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//				.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.O }
//				?: WindowManager.LayoutParams.TYPE_PHONE,
//			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
//					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//			PixelFormat.TRANSLUCENT
//		).apply {
//			gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
//			x = 0
//			y = 100
//		}
//		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//		windowManager.addView(overlayView, params)
//	}
//
//	private fun hideOverlay() {
//		if (::overlayView.isInitialized) {
//			windowManager.removeView(overlayView)
//			stopSelf()
//		}
//	}
//}


class OverlayService() : Service() {
	private var overlayDialog: Dialog? = null

	override fun onBind(intent: Intent?): IBinder? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		showOverlay()
		return START_STICKY
	}

	override fun onDestroy() {
		overlayDialog?.dismiss()
		overlayDialog = null
		super.onDestroy()
	}


	@SuppressLint("ClickableViewAccessibility")
	private fun showOverlay() {
		if (overlayDialog?.isShowing == true) return

		overlayDialog = Dialog(this).apply {
			setCanceledOnTouchOutside(true)
			setCancelable(true)

			window?.apply {
				setType(
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
						WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
					else
						WindowManager.LayoutParams.TYPE_PHONE
				)

				setLayout(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.WRAP_CONTENT
				)

				attributes = attributes.apply {
					gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
					y = 100   // отступ от верхнего края
					dimAmount = 0f
				}
			}

			setContentView(
				ComposeView(context).apply {
					setContent {
						// Ваш Compose UI
//						OverlayWindow(
//							onItemClick = { item ->
//				                viewModel.select(item)
//								setPrimaryClip(
//									this@OverlayService,
//									item.content,
//									item.type
//								)
//								dismiss()
//							},
//							onDismiss = {
//								dismiss()
//							},
//			                viewModel = viewModel
//						)
					}
				}
			)

			window?.decorView?.setOnTouchListener { _, event ->
				if (event.action == MotionEvent.ACTION_DOWN) dismiss()
				false
			}

			show()
		}
	}
}
