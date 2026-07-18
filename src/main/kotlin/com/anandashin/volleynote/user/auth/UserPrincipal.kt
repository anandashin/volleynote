package com.anandashin.volleynote.user.auth

import com.anandashin.volleynote.user.domain.Role

data class UserPrincipal(
    val id: Long,
    val email: String,
    val nickname: String,
    val introduction: String?,
    val role: Role,
)
