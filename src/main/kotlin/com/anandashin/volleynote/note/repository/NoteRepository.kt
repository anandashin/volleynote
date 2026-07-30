package com.anandashin.volleynote.note.repository

import com.anandashin.volleynote.note.domain.NoteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoteRepository : JpaRepository<NoteEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): NoteEntity?

    fun findByAuthorIdAndDeletedAtIsNull(
        authorId: Long,
        pageable: Pageable,
    ): Page<NoteEntity>

    fun findByIsPublicTrueAndDeletedAtIsNull(pageable: Pageable): Page<NoteEntity>

    @Query(
        "select n from NoteEntity n " +
            "where n.deletedAt is null " +
            "and n.id in (select b.noteId from NoteBookmarkEntity b where b.userId = :userId)",
    )
    fun findBookmarkedNotes(
        userId: Long,
        pageable: Pageable,
    ): Page<NoteEntity>
}
