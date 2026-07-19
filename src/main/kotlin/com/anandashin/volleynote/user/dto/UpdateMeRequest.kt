package com.anandashin.volleynote.user.dto

import jakarta.validation.constraints.Size

data class UpdateMeRequest(
    @field:Size(min = 1, max = 16, message = "Nickname must be between 1 and 16 characters")
    val nickname: String? = null,
    @field:Size(max = 50, message = "Introduction must be under 50 characters")
    val introduction: String? = null,
)
