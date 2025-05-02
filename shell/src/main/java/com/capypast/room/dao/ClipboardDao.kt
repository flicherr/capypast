package com.capypast.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.capypast.room.entities.ClipboardEntity

@Dao
interface ClipboardDao {
	@Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
	suspend fun insert(clip: ClipboardEntity)

	@Update
	suspend fun update(clip: ClipboardEntity)

	@Transaction
	suspend fun upsert(clip: ClipboardEntity) {
		val existing = existingItemText(clip.content)
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
	suspend fun delete(clip: ClipboardEntity)

	@Query("SELECT * FROM clipboard WHERE content = :content LIMIT 1")
	suspend fun existingItemText(content: String): ClipboardEntity?

	@Query("SELECT * FROM clipboard ORDER BY timestamp DESC LIMIT 1")
	suspend fun getLastClip(): ClipboardEntity?

	@Query("""
        SELECT * FROM clipboard
        WHERE (content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY pinned DESC, timestamp DESC
    """)
	fun searchPaged(query: String): PagingSource<Int, ClipboardEntity>

	@Query("""
        SELECT * FROM clipboard
        ORDER BY pinned DESC, timestamp DESC
    """)
	fun getAllPaged(): PagingSource<Int, ClipboardEntity>

	@Query("DELETE FROM clipboard")
	suspend fun clear()
}
