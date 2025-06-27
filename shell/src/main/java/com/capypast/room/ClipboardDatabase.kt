package com.capypast.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

	companion object {
		private var INSTANCE: ClipboardDatabase? = null

		fun getInstance(context: Context): ClipboardDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
			}

		private fun buildDatabase(context: Context): ClipboardDatabase =
			Room.databaseBuilder(
				context.applicationContext,
				ClipboardDatabase::class.java,
				"cp.db"
			)
				.enableMultiInstanceInvalidation()
				.fallbackToDestructiveMigration(true)
				.build()
	}
}
