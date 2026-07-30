package com.anandashin.volleynote.note.dto

import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateNoteRequest(
    @field:Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    val title: String? = null,
    @field:Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    val content: String? = null,
    val matchDate: LocalDate? = null,
    @field:Size(max = 50, message = "Home team must be under 50 characters")
    val homeTeam: String? = null,
    @field:Size(max = 50, message = "Away team must be under 50 characters")
    val awayTeam: String? = null,
    val isPublic: Boolean? = null,
)
