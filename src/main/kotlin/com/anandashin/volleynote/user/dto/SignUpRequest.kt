package com.anandashin.volleynote.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field: NotBlank(message = "Email cannot be blank")
    @field: Email(message = "Please enter a valid email address")
    val email: String,

    @field: Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @field: Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)[A-Za-z\\\\d]{8,16}\$",
        message = "Password should include uppercase letters, lowercase letters, and numbers."
    )
    val password: String,

    @field: Size(min = 1, max = 16, message = "Nickname must be between 1 and 16 characters")
    val nickname: String,

    @field: Size(max = 50, message = "Introduction must be under 50 characters")
    val introduction: String? = null,
) {
}
