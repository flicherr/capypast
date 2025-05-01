package com.capypast.room.interactors

import androidx.room.withTransaction
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.entities.TrashEntity
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.room.repositories.TrashRepository

class RestoreFromTrashInteractor(
    private val clipboardRepo: ClipboardRepository,
    private val trashRepo: TrashRepository,
    private val db: ClipboardDatabase
) {
    suspend operator fun invoke() = db.withTransaction {
        val allTrash = trashRepo.getAllItems()
        trashRepo.clear()
        allTrash.forEach { trash ->
            val clip = ClipboardEntity(
                timestamp   = trash.historyTimestamp,
                type        = trash.type,
                content     = trash.content,
                imagePath   = trash.imagePath,
                tags        = trash.tags,
                pinned      = trash.pinned,
                protected   = trash.protected
            )
            clipboardRepo.insert(clip)
        }
    }

    suspend operator fun invoke(trash: TrashEntity) {
        db.withTransaction {
            trashRepo.delete(trash)
            val clip = ClipboardEntity(
                timestamp   = trash.historyTimestamp,
                type        = trash.type,
                content     = trash.content,
                imagePath   = trash.imagePath,
                tags        = trash.tags,
                pinned      = trash.pinned,
                protected   = trash.protected
            )
            clipboardRepo.insert(clip)
        }
    }
}
