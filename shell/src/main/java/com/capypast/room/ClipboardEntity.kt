package com.capypast.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class ClipType {
    TEXT,
    IMAGE
}

class ClipTypeConverter {
    @TypeConverter fun fromEnum(type: ClipType): String = type.name
    @TypeConverter fun toEnum(type: String): ClipType = ClipType.valueOf(type)
}

@Entity(
    tableName = "clipboard",
    indices = [
        Index(value = ["content"]),
        Index(value = ["tags"])
    ]
)
data class ClipboardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: ClipType,
    val content: String?,
    @ColumnInfo(name = "image_path")
    val imagePath: String?,
    val tags: String = ""
)
