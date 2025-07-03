package com.capypast.room.interactors

import androidx.room.withTransaction
import com.capypast.room.ClipboardDatabase
import com.capypast.room.dao.ClipDao
import com.capypast.room.dao.TrashDao
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.TrashEntity

class RestoreFromTrashInteractor(
	private val db: ClipboardDatabase,
	private val clipDao: ClipDao,
	private val trashDao: TrashDao,
) {
	suspend operator fun invoke() = db.withTransaction {
		val allTrash = trashDao.allItems()
		trashDao.clear()
		allTrash.forEach { trash ->
			val clip = ClipEntity(
				timestamp   = trash.historyTimestamp,
				type        = trash.type,
				content     = trash.content,
				pinned      = trash.pinned,
				isProtected   = trash.isProtected,
				tags        = trash.tags,
			)
			clipDao.insert(clip)
		}
	}

	suspend operator fun invoke(trash: TrashEntity) {
		db.withTransaction {
			trashDao.delete(trash)
			val clip = ClipEntity(
				timestamp   = trash.historyTimestamp,
				type        = trash.type,
				content     = trash.content,
				pinned      = trash.pinned,
				isProtected   = trash.isProtected,
				tags        = trash.tags,
			)
			clipDao.insert(clip)
		}
	}
}
