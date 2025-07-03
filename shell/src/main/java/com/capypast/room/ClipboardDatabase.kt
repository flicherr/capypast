package com.capypast.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.capypast.room.dao.ClipDao
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.ClipTypeConverter
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.TrashEntity

@Database(
	entities = [ClipEntity::class, TrashEntity::class],
	version = 1,
	exportSchema = false,
)
@TypeConverters(ClipTypeConverter::class)
abstract class ClipboardDatabase : RoomDatabase() {
	abstract fun clipDao(): ClipDao
	abstract fun trashDao(): TrashDao
}
