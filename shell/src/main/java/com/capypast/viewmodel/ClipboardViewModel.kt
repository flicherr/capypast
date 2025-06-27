package com.capypast.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.ClipType
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClipboardViewModel(
	private val repo: ClipboardRepository,
	private val toTrash: MoveToTrashInteractor
) : ViewModel() {
	val searchQuery = MutableStateFlow<String?>(null)
	private val searchBarVisible = mutableStateOf(false)

	internal val filter = MutableStateFlow(setOf(ClipType.TEXT, ClipType.IMAGE))

	private val isSelectionMode: Boolean
		get() = selectedItems.isNotEmpty()

	var selectedItems by mutableStateOf<Set<ClipEntity>>(emptySet())
		private set

	var protectedClips by mutableStateOf<Set<ClipEntity>>(emptySet())
		private set

	fun addReadAccessClip(clip: ClipEntity) {
		protectedClips = protectedClips.toMutableSet().apply {
				add(clip)
		}
	}
	fun removeReadAccessClip(clip: ClipEntity) {
		protectedClips = protectedClips.toMutableSet().apply {
			remove(clip)
		}
	}
	fun clearReadAccessClips() {
		protectedClips = protectedClips.toMutableSet().apply {
			clear()
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	val itemsFlow: Flow<PagingData<ClipEntity>> = searchQuery
		.flatMapLatest { query ->
			if (query.isNullOrBlank()) {
				filter
					.flatMapLatest { types ->
						repo.filterFlow(types.toList()).flow
					}
			} else {
				filter
					.flatMapLatest { types ->
						repo.filterSearch(types.toList(), query).flow
					}
			}
		}.cachedIn(viewModelScope)

	fun moveToTrash(clip: ClipEntity) {
		viewModelScope.launch {
			toTrash(clip)
		}
	}

	fun setPinned(clip: ClipEntity) {
		viewModelScope.launch {
			repo.setPinned(clip.id, !clip.pinned)
		}
	}

	fun setProtected(clip: ClipEntity) {
		viewModelScope.launch {
			repo.setProtected(clip.id, !clip.isProtected)
		}
	}


	/*------------------------ search -----------------------*/
	fun toggleSearchBarAction() {
		searchBarVisible.value = !searchBarVisible.value
	}

	fun searchBarAction(): Boolean = searchBarVisible.value

	fun search(query: String) {
		searchQuery.value = query
	}


	/*------------------------ filter -----------------------*/
	fun toggleFilter(type: ClipType) {
		filter.update { current ->
			if (type in current) current - type else current + type
		}
	}


	/*---------------------- selected ----------------------*/
	fun selectionMode(): Boolean = isSelectionMode

	fun toggleSelection(item: ClipEntity) {
		selectedItems = selectedItems.toMutableSet().apply {
			if (contains(item)) {
				remove(item)
			} else {
				add(item)
			}
		}
	}

	fun selectAll() {
		CoroutineScope(Dispatchers.Main).launch {
			selectedItems =
				repo.getAll()?.toSet()
					.takeIf { searchQuery.value.isNullOrBlank() }
					?: repo.searchResult(searchQuery.toString())?.toSet()
							?: return@launch
		}
	}

	fun clearSelection() {
		selectedItems = emptySet()
	}

	fun deleteSelected() {
		viewModelScope.launch {
			for (clip in selectedItems.toList()) {
				moveToTrash(clip)
			}
			selectedItems = emptySet()
		}
	}
}