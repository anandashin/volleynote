package com.anandashin.volleynote.note.repository

import com.anandashin.volleynote.note.domain.NoteLikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoteLikeRepository : JpaRepository<NoteLikeEntity, Long> {
    fun existsByUserIdAndNoteId(
        userId: Long,
        noteId: Long,
    ): Boolean

    fun findByUserIdAndNoteId(
        userId: Long,
        noteId: Long,
    ): NoteLikeEntity?

    @Query("select l.noteId from NoteLikeEntity l where l.userId = :userId and l.noteId in :noteIds")
    fun findLikedNoteIds(
        userId: Long,
        noteIds: Collection<Long>,
    ): List<Long>
}
