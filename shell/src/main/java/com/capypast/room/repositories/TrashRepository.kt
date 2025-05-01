package com.capypast.room.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.TrashEntity

class TrashRepository(
    private val dao: TrashDao,
) {

    suspend fun insert(trash: TrashEntity) = dao.insert(trash)

    suspend fun delete(trash: TrashEntity) = dao.delete(trash)

    suspend fun clear() = dao.clear()

    suspend fun getAllItems(): List<TrashEntity> = dao.getAllItems()

    fun getTrashFlow(): Pager<Int, TrashEntity> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { dao.getAllPaged() }
    )
}