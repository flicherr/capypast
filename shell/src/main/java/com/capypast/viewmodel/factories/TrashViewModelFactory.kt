package com.capypast.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.ClipboardDatabase
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.TrashRepository
import com.capypast.viewmodel.TrashViewModel

class TrashViewModelFactory(
	private val context: Context
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
			val db = ClipboardDatabase
				.getInstance(context)
			val repository = TrashRepository(db.trashDao())
			val interactor = RestoreFromTrashInteractor(db = db)

			return TrashViewModel(repository, interactor) as T
		}

		throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
	}
}