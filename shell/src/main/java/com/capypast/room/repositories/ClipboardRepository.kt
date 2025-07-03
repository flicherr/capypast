package com.capypast.room.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.capypast.room.dao.ClipDao
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.ClipType

class ClipboardRepository(private val dao: ClipDao) {

	suspend fun insert(clip: ClipEntity) = dao.insert(clip)

	suspend fun upsert(clip: ClipEntity) = dao.upsert(clip)

	suspend fun setPinned(id: Long, pinned: Boolean) = dao.setPinned(id, pinned)

	suspend fun setProtected(id: Long, isProtected: Boolean) = dao.setProtected(id, isProtected)

	suspend fun lastClip() = dao.lastClip()

	fun clipsFlow(): Pager<Int, ClipEntity> = Pager(
		config = PagingConfig(pageSize = 44),
		pagingSourceFactory = { dao.allPaged() }
	)

	fun searchFlow(query: String): Pager<Int, ClipEntity> = Pager(
		config = PagingConfig(pageSize = 44),
		pagingSourceFactory = { dao.searchPaged(query) }
	)

	suspend fun getAll(): List<ClipEntity>? = dao.allItems().toList()

	suspend fun searchResult(query: String): List<ClipEntity>? = dao.searchItems(query).toList()

	fun filterFlow(types: List<ClipType>): Pager<Int, ClipEntity> {
		return Pager(
			config = PagingConfig(pageSize = 44),
			pagingSourceFactory = { dao.itemsByTypes(types) }
		)
	}

	fun filterSearch(types: List<ClipType>, query: String): Pager<Int, ClipEntity> {
		return Pager(
			config = PagingConfig(pageSize = 44),
			pagingSourceFactory = { dao.itemsByTypesSearch(types, query) }
		)
	}
}