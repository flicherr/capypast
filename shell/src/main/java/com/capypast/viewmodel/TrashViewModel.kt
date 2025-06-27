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
	private val repo: TrashRepository,
	private val restoreInteract: RestoreFromTrashInteractor
) : ViewModel() {
	val trashPagingData: StateFlow<PagingData<TrashEntity>> =
		repo
			.trashFlow()
			.flow
			.cachedIn(viewModelScope)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.Lazily,
				initialValue = PagingData.empty()
			)

	val enabledTrash: StateFlow<Boolean> = repo
		.exists()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted
				.WhileSubscribed(5_000),
			initialValue = false
		)

	fun restore(trashItem: TrashEntity) {
		viewModelScope.launch {
			restoreInteract(trashItem)
		}
	}

	fun restoreAll() {
		viewModelScope.launch {
			restoreInteract()
		}
	}

	fun delete(trashItem: TrashEntity) {
		viewModelScope.launch {
			repo.delete(trashItem)
		}
	}

	fun deleteAll() {
		viewModelScope.launch {
			repo.clear()
		}
	}
}