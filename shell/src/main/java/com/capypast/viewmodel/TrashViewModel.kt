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
	private val repository: TrashRepository,
	private val interactor: RestoreFromTrashInteractor
) : ViewModel() {

	val trashPagingData: StateFlow<PagingData<TrashEntity>> =
		repository
			.getTrashFlow()
			.flow
			.cachedIn(viewModelScope)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.Lazily,
				initialValue = PagingData.empty()
			)

	val enabledTrash: StateFlow<Boolean> = repository
		.exists()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted
				.WhileSubscribed(5_000),
			initialValue = false
		)

	fun onRestore(trashItem: TrashEntity) {
		viewModelScope.launch {
			interactor(trashItem)
		}
	}

	fun onRestoreAll() {
		viewModelScope.launch {
			interactor()
		}
	}

	fun onDelete(trashItem: TrashEntity) {
		viewModelScope.launch {
			repository.delete(trashItem)
		}
	}

	fun onClearAll() {
		viewModelScope.launch {
			repository.clear()
		}
	}
}