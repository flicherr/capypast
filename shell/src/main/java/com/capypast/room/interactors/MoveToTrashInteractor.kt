package com.capypast.room.interactors

import androidx.room.withTransaction
import com.capypast.room.ClipboardDatabase
import com.capypast.room.entities.ClipboardEntity
import com.capypast.room.entities.TrashEntity
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.room.repositories.TrashRepository

class MoveToTrashInteractor(
    private val clipboardRepo: ClipboardRepository,
    private val trashRepo: TrashRepository,
    private val db: ClipboardDatabase
) {
    suspend operator fun invoke(clip: ClipboardEntity) {
        val now = System.currentTimeMillis()
        db.withTransaction {
            clipboardRepo.delete(clip)
            val trashItem = TrashEntity(
                timestamp           = now,
                historyTimestamp    = clip.timestamp,
                type                = clip.type,
                content             = clip.content,
                imagePath           = clip.imagePath,
                tags                = clip.tags,
                pinned              = clip.pinned,
                protected           = clip.protected
            )
            trashRepo.insert(trashItem)
        }
    }
}