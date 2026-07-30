package com.anandashin.volleynote.note.repository

import com.anandashin.volleynote.note.domain.NoteBookmarkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoteBookmarkRepository : JpaRepository<NoteBookmarkEntity, Long> {
    fun existsByUserIdAndNoteId(
        userId: Long,
        noteId: Long,
    ): Boolean

    fun findByUserIdAndNoteId(
        userId: Long,
        noteId: Long,
    ): NoteBookmarkEntity?

    @Query("select b.noteId from NoteBookmarkEntity b where b.userId = :userId and b.noteId in :noteIds")
    fun findBookmarkedNoteIds(
        userId: Long,
        noteIds: Collection<Long>,
    ): List<Long>
}
