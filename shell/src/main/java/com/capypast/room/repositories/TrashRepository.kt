package com.capypast.room.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.TrashEntity
import kotlinx.coroutines.flow.Flow

class TrashRepository(
	private val dao: TrashDao,
) {
	suspend fun insert(trash: TrashEntity) = dao.insert(trash)

	suspend fun delete(trash: TrashEntity) = dao.delete(trash)

	fun exists(): Flow<Boolean> = dao.exists()

	suspend fun clear() = dao.clear()

	fun trashFlow(): Pager<Int, TrashEntity> = Pager(
		config = PagingConfig(pageSize = 20),
		pagingSourceFactory = { dao.allPaged() }
	)
}