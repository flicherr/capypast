package com.capypast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capypast.room.entities.TrashEntity
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.TrashRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrashViewModel(
    private val trashRepository: TrashRepository,
    private val restore: RestoreFromTrashInteractor,
    ) : ViewModel() {

    val trashPagingData: StateFlow<PagingData<TrashEntity>> =
        trashRepository
            .getTrashFlow()
            .flow
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = PagingData.empty()
            )

    fun onRestore(trashItem: TrashEntity) {
        viewModelScope.launch {
            restore(trashItem)
        }
    }

    fun onRestoreAll() {
        viewModelScope.launch {
            restore()
        }
    }

    fun onDelete(trashItem: TrashEntity) {
        viewModelScope.launch {
            trashRepository.delete(trashItem)
        }
    }

    fun onClearAll() {
        viewModelScope.launch {
            trashRepository.clear()
        }
    }
}