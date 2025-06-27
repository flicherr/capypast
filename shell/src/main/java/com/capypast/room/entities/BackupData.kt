package com.capypast.room.entities

data class BackupData(
	val clipboard: List<ClipEntity>,
	val trashcan: List<TrashEntity>
)