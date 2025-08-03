package com.anandashin.volleynote.user.repository

import com.anandashin.volleynote.user.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByNickname(nickname: String): UserEntity?
    fun existsByEmail(email: String): Boolean
}
// findById, existsById는 기본 제공. 따로 선언 X
