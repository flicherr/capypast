package com.capypast.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capypast.room.entities.TrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(trash: TrashEntity)

	@Delete
	suspend fun delete(trash: TrashEntity)

	@Query("SELECT EXISTS(SELECT 1 FROM trash)")
	fun exists(): Flow<Boolean>

	@Query("SELECT * FROM trash ORDER BY timestamp DESC")
	fun getAllPaged(): PagingSource<Int, TrashEntity>

	@Query("SELECT * FROM trash")
	suspend fun getAllItems(): List<TrashEntity>

	@Query("DELETE FROM trash")
	suspend fun clear()
}
