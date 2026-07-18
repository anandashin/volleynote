package com.anandashin.volleynote.user.service

import com.anandashin.volleynote.user.LoginInvalidPasswordException
import com.anandashin.volleynote.user.LoginUserNotFoundException
import com.anandashin.volleynote.user.SignUpEmailConflictException
import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.dto.UserDTO
import com.anandashin.volleynote.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.mindrot.jbcrypt.BCrypt
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
        return "Success"
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
}
