package com.anandashin.volleynote.user.service

import com.anandashin.volleynote.user.dto.SignUpRequest

interface UserService {
    fun createUser(email: String, password: String, nickname: String, introduction: String?): String
}
