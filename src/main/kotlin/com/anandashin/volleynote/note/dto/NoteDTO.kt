package com.anandashin.volleynote.note.dto

import com.anandashin.volleynote.note.domain.NoteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class NoteDTO(
    val id: Long,
    val authorId: Long,
    val title: String,
    val content: String,
    val matchDate: LocalDate?,
    val homeTeam: String?,
    val awayTeam: String?,
    val isPublic: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: NoteEntity): NoteDTO =
            NoteDTO(
                id = entity.id,
                authorId = entity.authorId,
                title = entity.title,
                content = entity.content,
                matchDate = entity.matchDate,
                homeTeam = entity.homeTeam,
                awayTeam = entity.awayTeam,
                isPublic = entity.isPublic,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
    }
}
