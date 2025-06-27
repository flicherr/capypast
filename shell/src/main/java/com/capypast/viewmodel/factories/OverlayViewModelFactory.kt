package com.capypast.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.ClipboardDatabase
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.viewmodel.OverlayViewModel

class OverlayViewModelFactory(
	private val context: Context
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(OverlayViewModel::class.java)) {
			val db = ClipboardDatabase
				.getInstance(context.applicationContext)
			val repository = ClipboardRepository(db.clipDao())

			return OverlayViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
	}
}