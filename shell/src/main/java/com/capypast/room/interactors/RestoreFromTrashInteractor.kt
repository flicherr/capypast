package com.capypast.room.interactors

import androidx.room.withTransaction
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipEntity
import com.capypast.room.entities.TrashEntity

class RestoreFromTrashInteractor(
	private val db: ClipboardDatabase
) {
	suspend operator fun invoke() = db.withTransaction {
		val allTrash = db.trashDao().allItems()
		db.trashDao().clear()
		allTrash.forEach { trash ->
			val clip = ClipEntity(
				timestamp   = trash.historyTimestamp,
				type        = trash.type,
				content     = trash.content,
				pinned      = trash.pinned,
				isProtected   = trash.isProtected,
				tags        = trash.tags,
			)
			db.clipDao().insert(clip)
		}
	}

	suspend operator fun invoke(trash: TrashEntity) {
		db.withTransaction {
			db.trashDao().delete(trash)
			val clip = ClipEntity(
				timestamp   = trash.historyTimestamp,
				type        = trash.type,
				content     = trash.content,
				pinned      = trash.pinned,
				isProtected   = trash.isProtected,
				tags        = trash.tags,
			)
			db.clipDao().insert(clip)
		}
	}
}
