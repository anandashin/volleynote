package com.anandashin.volleynote.user.dto

data class SignInResponse(
    val userId: Long,
    val accessToken: String,
) {
}
