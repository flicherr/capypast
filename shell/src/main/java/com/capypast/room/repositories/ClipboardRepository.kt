package com.capypast.room.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.capypast.room.dao.ClipboardDao
import com.capypast.room.entities.ClipboardEntity

class ClipboardRepository(private val dao: ClipboardDao) {

    suspend fun insert(clip: ClipboardEntity) = dao.insert(clip)

    suspend fun upsert(clip: ClipboardEntity) = dao.upsert(clip)

    suspend fun setPinned(id: Long, pinned: Boolean) = dao.setPinned(id, pinned)

    suspend fun setProtected(id: Long, isProtected: Boolean) = dao.setProtected(id, isProtected)

    suspend fun delete(clip: ClipboardEntity) = dao.delete(clip)

    suspend fun clear() = dao.clear()

    suspend fun getLastClip() = dao.getLastClip()

    fun getHistoryFlow(): Pager<Int, ClipboardEntity> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { dao.getAllPaged() }
    )

    fun searchFlow(query: String): Pager<Int, ClipboardEntity> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { dao.searchPaged(query) }
    )
}