package com.anandashin.volleynote.note.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateNoteRequest(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(max = 100, message = "Title must be under 100 characters")
    val title: String,
    @field:NotBlank(message = "Content cannot be blank")
    @field:Size(max = 5000, message = "Content must be under 5000 characters")
    val content: String,
    val matchDate: LocalDate? = null,
    @field:Size(max = 50, message = "Home team must be under 50 characters")
    val homeTeam: String? = null,
    @field:Size(max = 50, message = "Away team must be under 50 characters")
    val awayTeam: String? = null,
    val isPublic: Boolean = true,
)
