package com.capypast.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.ClipboardDatabase
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.room.repositories.TrashRepository
import com.capypast.viewmodel.ClipboardViewModel

class ClipboardViewModelFactory(
	private val context: Context
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
			val db = ClipboardDatabase
				.getInstance(context)
			val repository = ClipboardRepository(db.clipboardDao())
			val interactor = MoveToTrashInteractor(db = db)

			return ClipboardViewModel(repository, interactor) as T
		}

		throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
	}
}