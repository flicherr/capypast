package com.capypast.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trash",
    indices = [
        Index(value = ["content"]),
        Index(value = ["tags"])
    ]
)
data class TrashEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    @ColumnInfo(name = "history_timestamp") val historyTimestamp: Long,
    val type: ClipType,
    val content: String?,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    val tags: String = "",
    val pinned: Boolean = false,
    val protected: Boolean = false
)
