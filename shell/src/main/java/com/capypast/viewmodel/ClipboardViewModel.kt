package com.capypast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ClipboardViewModel(
    private val repository: ClipboardRepository,
    private val moveToTrashInteractor: MoveToTrashInteractor
) : ViewModel() {

    private val currentQuery = MutableStateFlow<String?>(null)

    fun insert(clip: ClipboardEntity) {
        viewModelScope.launch {
            repository.insert(clip)
        }
    }

    fun moveToTrash(clip: ClipboardEntity) {
        viewModelScope.launch {
            moveToTrashInteractor(clip)
        }
    }

    fun setPinned(clip: ClipboardEntity) {
        viewModelScope.launch {
            repository.setPinned(clip.id, !clip.pinned)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemsFlow: Flow<PagingData<ClipboardEntity>> = currentQuery
        .flatMapLatest { query ->
            if (query.isNullOrBlank()) {
                repository.getHistoryFlow().flow
            } else {
                repository.searchFlow(query).flow
            }
        }
        .cachedIn(viewModelScope)



    fun search(query: String) {
        currentQuery.value = query
    }
}