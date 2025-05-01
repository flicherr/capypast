package com.capypast.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.capypast.room.dao.ClipboardDao
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.ClipTypeConverter
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.entities.TrashEntity

@Database(
    entities = [ClipboardEntity::class, TrashEntity::class],
    version = 1,
)
@TypeConverters(ClipTypeConverter::class)
abstract class ClipboardDatabase : RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao
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


        // миграция 1 → 2
//            .addMigrations(
//                    object : Migration(1, 2) {
//                        override fun migrate(db: SupportSQLiteDatabase) {
//                            db.execSQL("""
//                                CREATE TABLE IF NOT EXISTS `trash` (
//                                  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                                  `timestamp` INTEGER NOT NULL,
//                                  `history_timestamp` INTEGER NOT NULL,
//                                  `type` TEXT NOT NULL,
//                                  `content` TEXT,
//                                  `image_path` TEXT,
//                                  `tags` TEXT NOT NULL
//                                )
//                            """.trimIndent()
//                            )
//                            db.execSQL("""
//                              ALTER TABLE `clipboard`
//                              ADD COLUMN `pinned` INTEGER NOT NULL DEFAULT 0
//                            """.trimIndent()
//                            )
//                            db.execSQL("""
//                              ALTER TABLE `clipboard`
//                              ADD COLUMN `protected` INTEGER NOT NULL DEFAULT 0
//                            """.trimIndent()
//                            )
//                            db.execSQL("""
//                              ALTER TABLE `trash`
//                              ADD COLUMN `pinned` INTEGER NOT NULL DEFAULT 0
//                            """.trimIndent()
//                            )
//                            db.execSQL("""
//                              ALTER TABLE `trash`
//                              ADD COLUMN `protected` INTEGER NOT NULL DEFAULT 0
//                            """.trimIndent()
//                            )
//                        }
//                    }
//                )

