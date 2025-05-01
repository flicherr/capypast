package com.capypast.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.TrashRepository
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.TrashViewModel

class TrashViewModelFactory(
    private val repository: TrashRepository,
    private val restoreFromTrashInteractor: RestoreFromTrashInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
            return TrashViewModel(
                repository,
                restoreFromTrashInteractor,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}