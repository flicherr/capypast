package com.capypast.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClipboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clip: ClipboardEntity)

    @Query("SELECT * FROM clipboard ORDER BY timestamp DESC")
    fun getAllPaged(): PagingSource<Int, ClipboardEntity>

    @Query("""
        SELECT * FROM clipboard 
        WHERE content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchPaged(query: String): PagingSource<Int, ClipboardEntity>

    @Delete
    suspend fun delete(clip: ClipboardEntity)

    @Query("DELETE FROM clipboard")
    suspend fun clear()

    // Поиск дубликатов по тексту
    @Query("SELECT * FROM clipboard WHERE content = :content LIMIT 1")
    suspend fun existingItemText(content: String): ClipboardEntity?

//    // Поиск дубликатов по картинке
//    @Query("SELECT * FROM clipboard WHERE content = :imagePath LIMIT 1")
//    suspend fun existingItemImg(imagePath: String): ClipboardEntity?

    // Получение последней записи
    @Query("SELECT * FROM clipboard ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastClip(): ClipboardEntity?
}
