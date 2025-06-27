package com.capypast.service.overlay

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

class FakeLifecycleOwner() : SavedStateRegistryOwner, ViewModelStoreOwner, LifecycleOwner {
	private val lifecycleRegistry = LifecycleRegistry(this)
	override val lifecycle: Lifecycle
		get() = lifecycleRegistry

	private val store = ViewModelStore()
	override val viewModelStore: ViewModelStore
		get() = store

	private lateinit var savedStateController: SavedStateRegistryController
	override val savedStateRegistry: SavedStateRegistry
		get() = savedStateController.savedStateRegistry

	fun initialize() {
		savedStateController = SavedStateRegistryController.create(this)
		savedStateController.performAttach()

		lifecycleRegistry.currentState = Lifecycle.State.CREATED
	}

	fun attachToView(view: View) {
		view.setViewTreeLifecycleOwner(this)
		view.setViewTreeViewModelStoreOwner(this)
		view.setViewTreeSavedStateRegistryOwner(this)
	}

	fun moveToStarted() {
//		lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
		lifecycleRegistry.currentState = Lifecycle.State.STARTED
	}

	fun moveToResumed() {
//		lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
		lifecycleRegistry.currentState = Lifecycle.State.RESUMED
	}

	fun destroy() {
//		lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
		lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
		viewModelStore.clear()
	}
}