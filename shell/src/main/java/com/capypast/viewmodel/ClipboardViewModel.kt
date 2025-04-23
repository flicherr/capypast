package com.capypast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capypast.room.ClipboardEntity
import com.capypast.room.ClipboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ClipboardViewModel(
    private val repository: ClipboardRepository
) : ViewModel() {

    private val currentQuery = MutableStateFlow<String?>(null)

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

    fun insert(clip: ClipboardEntity) {
        viewModelScope.launch {
            repository.insert(clip)
        }
    }

   fun delete(clip: ClipboardEntity) {
        viewModelScope.launch {
            repository.delete(clip)
        }
    }

    fun clear() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun search(query: String) {
        currentQuery.value = query
    }
}

class ClipboardViewModelFactory(
    private val repository: ClipboardRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
            return ClipboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}