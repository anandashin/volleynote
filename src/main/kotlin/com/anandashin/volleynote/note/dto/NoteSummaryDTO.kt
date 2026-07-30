package com.anandashin.volleynote.note.dto

import com.anandashin.volleynote.note.domain.NoteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class NoteSummaryDTO(
    val id: Long,
    val authorId: Long,
    val title: String,
    val matchDate: LocalDate?,
    val homeTeam: String?,
    val awayTeam: String?,
    val isPublic: Boolean,
    val likeCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: NoteEntity,
            isLiked: Boolean,
            isBookmarked: Boolean,
        ): NoteSummaryDTO =
            NoteSummaryDTO(
                id = entity.id,
                authorId = entity.authorId,
                title = entity.title,
                matchDate = entity.matchDate,
                homeTeam = entity.homeTeam,
                awayTeam = entity.awayTeam,
                isPublic = entity.isPublic,
                likeCount = entity.likeCount,
                isLiked = isLiked,
                isBookmarked = isBookmarked,
                createdAt = entity.createdAt,
            )
    }
}
