package com.capypast.room.interactors

import androidx.room.withTransaction
import com.capypast.room.ClipboardDatabase
import com.capypast.room.dao.ClipDao
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.TrashEntity

class MoveToTrashInteractor(
	private val db: ClipboardDatabase,
	private val clipDao: ClipDao,
	private val trashDao: TrashDao,
) {
	suspend operator fun invoke(clip: ClipEntity) {
		val now = System.currentTimeMillis()
		db.withTransaction {
			clipDao.delete(clip)
			val trashItem = TrashEntity(
				timestamp           = now,
				historyTimestamp    = clip.timestamp,
				type                = clip.type,
				content             = clip.content,
				pinned              = clip.pinned,
				isProtected         = clip.isProtected,
				tags                = clip.tags,
			)
			trashDao.insert(trashItem)
		}
	}
}