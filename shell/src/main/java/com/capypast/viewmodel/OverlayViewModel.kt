package com.capypast.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capypast.room.entities.ClipEntity
import com.capypast.room.repositories.ClipboardRepository
import kotlinx.coroutines.flow.Flow

class OverlayViewModel(
	repo: ClipboardRepository,
) : ViewModel() {
	var protectedClip: ClipEntity? by mutableStateOf(null)
		private set

	val itemsFlow: Flow<PagingData<ClipEntity>> = repo.clipsFlow()
		.flow
		.cachedIn(viewModelScope)

	fun readAccess(clip: ClipEntity?) {
		protectedClip = clip
	}
}