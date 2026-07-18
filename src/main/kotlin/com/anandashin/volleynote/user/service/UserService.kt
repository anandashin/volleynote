package com.anandashin.volleynote.user.service

import com.anandashin.volleynote.user.LoginInvalidPasswordException
import com.anandashin.volleynote.user.LoginUserNotFoundException
import com.anandashin.volleynote.user.SignUpEmailConflictException
import com.anandashin.volleynote.user.UserNotFoundException
import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.dto.PublicUserDTO
import com.anandashin.volleynote.user.dto.UpdateMeRequest
import com.anandashin.volleynote.user.dto.UserDTO
import com.anandashin.volleynote.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.mindrot.jbcrypt.BCrypt
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

interface UserService {
    fun createUser(
        email: String,
        password: String,
        nickname: String,
        introduction: String?,
    ): String

    fun login(
        email: String,
        password: String,
    ): Pair<UserDTO, String>

    fun updateMe(
        userId: Long,
        request: UpdateMeRequest,
    ): UserDTO

    fun deleteMe(userId: Long)

    fun getPublicProfile(userId: Long): PublicUserDTO
}

@Service
open class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    @Transactional
    override fun createUser(
        email: String,
        password: String,
        nickname: String,
        introduction: String?,
    ): String {
        if (userRepository.existsByEmail(email)) {
            throw SignUpEmailConflictException()
        }
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        userRepository.save(
            UserEntity(
                email = email,
                hashedPassword = hashedPassword,
                nickname = nickname,
                introduction = introduction,
            ),
        )
        return "success"
    }

    @Transactional
    override fun login(
        email: String,
        password: String,
    ): Pair<UserDTO, String> {
        val user = userRepository.findByEmail(email) ?: throw LoginUserNotFoundException()
        if (!BCrypt.checkpw(password, user.hashedPassword)) {
            throw LoginInvalidPasswordException()
        }
        val accessToken = JwtTokenProvider.createJwtToken(user.id)
        return Pair(UserDTO.from(user), accessToken)
    }

    @Transactional
    override fun updateMe(
        userId: Long,
        request: UpdateMeRequest,
    ): UserDTO {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        request.nickname?.let { user.nickname = it }
        request.introduction?.let { user.introduction = it }
        return UserDTO.from(user)
    }

    @Transactional
    override fun deleteMe(userId: Long) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        userRepository.delete(user)
    }

    override fun getPublicProfile(userId: Long): PublicUserDTO {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        return PublicUserDTO.from(user)
    }
}
