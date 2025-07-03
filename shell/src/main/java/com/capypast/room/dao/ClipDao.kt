package com.capypast.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.ClipType

@Dao
interface ClipDao {
	@Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
	suspend fun insert(clip: ClipEntity)

	@Update
	suspend fun update(clip: ClipEntity)

	@Transaction
	suspend fun upsert(clip: ClipEntity) {
		val existing = existByText(clip.content)
		if (existing != null) {
			update(existing.copy(timestamp = clip.timestamp))
		} else {
			insert(clip)
		}
	}

	@Query("UPDATE clipboard SET pinned = :pinned WHERE id = :id")
	suspend fun setPinned(id: Long, pinned: Boolean)

	@Query("UPDATE clipboard SET protected = :isProtected WHERE id = :id")
	suspend fun setProtected(id: Long, isProtected: Boolean)

	@Delete
	suspend fun delete(clip: ClipEntity)

	@Query("SELECT * FROM clipboard WHERE content = :content LIMIT 1")
	suspend fun existByText(content: String): ClipEntity?

	@Query("SELECT * FROM clipboard ORDER BY timestamp DESC LIMIT 1")
	suspend fun lastClip(): ClipEntity?

	@Query("""
        SELECT * FROM clipboard
        WHERE (content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY pinned DESC, timestamp DESC
    """)
	fun searchPaged(query: String): PagingSource<Int, ClipEntity>

	@Query("""
        SELECT * FROM clipboard
        WHERE (content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY pinned DESC, timestamp DESC
    """)
	suspend fun searchItems(query: String): List<ClipEntity>

	@Query("SELECT * FROM clipboard WHERE type IN (:types) ORDER BY pinned DESC, timestamp DESC")
	fun itemsByTypes(types: List<ClipType>): PagingSource<Int, ClipEntity>

	@Query("""
        SELECT * FROM clipboard
        WHERE (type IN (:types)) AND (content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY pinned DESC, timestamp DESC
    """)
	fun itemsByTypesSearch(types: List<ClipType>, query: String): PagingSource<Int, ClipEntity>

	@Query("""
        SELECT * FROM clipboard
        ORDER BY pinned DESC, timestamp DESC
    """)
	fun allPaged(): PagingSource<Int, ClipEntity>

	@Query("""
        SELECT * FROM clipboard
        ORDER BY pinned DESC, timestamp DESC
    """)
	suspend fun allItems(): List<ClipEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSome(items: List<ClipEntity>)
}
