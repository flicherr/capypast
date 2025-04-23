package com.capypast.room

import androidx.paging.Pager
import androidx.paging.PagingConfig

class ClipboardRepository(private val dao: ClipboardDao) {
    fun getHistoryFlow(): Pager<Int, ClipboardEntity> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { dao.getAllPaged() }
    )

    fun searchFlow(query: String): Pager<Int, ClipboardEntity> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { dao.searchPaged(query) }
    )

    suspend fun insert(clip: ClipboardEntity) = dao.insert(clip)

    suspend fun delete(clip: ClipboardEntity) = dao.delete(clip)

    suspend fun clear() = dao.clear()
}
