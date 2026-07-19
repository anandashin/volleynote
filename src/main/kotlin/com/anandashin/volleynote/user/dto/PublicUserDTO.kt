package com.anandashin.volleynote.user.dto

import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity

data class PublicUserDTO(
    val id: Long,
    val nickname: String,
    val introduction: String?,
    val role: Role,
) {
    companion object {
        fun from(entity: UserEntity): PublicUserDTO =
            PublicUserDTO(
                id = entity.id,
                nickname = entity.nickname,
                introduction = entity.introduction,
                role = entity.role,
            )
    }
}
