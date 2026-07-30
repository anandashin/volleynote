package com.anandashin.volleynote.note.repository

import com.anandashin.volleynote.note.domain.NoteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<NoteEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): NoteEntity?

    fun findByAuthorIdAndDeletedAtIsNull(
        authorId: Long,
        pageable: Pageable,
    ): Page<NoteEntity>

    fun findByIsPublicTrueAndDeletedAtIsNull(pageable: Pageable): Page<NoteEntity>
}
