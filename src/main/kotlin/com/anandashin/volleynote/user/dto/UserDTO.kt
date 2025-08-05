package com.anandashin.volleynote.user.dto

import com.anandashin.volleynote.user.domain.UserEntity

data class UserDTO(
    val id: Long,
    val email: String,
    val nickname: String,
    val introduction: String? = null,
) {
    companion object{
        fun from(entity: UserEntity): UserDTO {
            return UserDTO(
                id = entity.id,
                email = entity.email,
                nickname = entity.nickname,
                introduction = entity.introduction,
            )
        }
    }
}
