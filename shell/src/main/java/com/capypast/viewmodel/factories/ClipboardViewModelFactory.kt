package com.capypast.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.viewmodel.ClipboardViewModel

class ClipboardViewModelFactory(
    private val repository: ClipboardRepository,
    private val moveToTrashInteractor: MoveToTrashInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
            return ClipboardViewModel(repository, moveToTrashInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}