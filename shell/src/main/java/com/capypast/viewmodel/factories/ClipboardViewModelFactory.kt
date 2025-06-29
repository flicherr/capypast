package com.capypast.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.ClipboardDatabase
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.viewmodel.ClipboardViewModel

class ClipboardViewModelFactory(
	private val context: Context
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
			val db = ClipboardDatabase
				.getInstance(context)
			val repo = ClipboardRepository(db.clipDao())
			val interact = MoveToTrashInteractor(db = db)

			return ClipboardViewModel(repo, interact) as T
		}

		throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
	}
}