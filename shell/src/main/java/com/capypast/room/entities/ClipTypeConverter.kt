package com.capypast.room.entities

import androidx.room.TypeConverter

class ClipTypeConverter {
	@TypeConverter fun fromEnum(type: ClipType): String = type.name
	@TypeConverter fun toEnum(type: String): ClipType = ClipType.valueOf(type)
}