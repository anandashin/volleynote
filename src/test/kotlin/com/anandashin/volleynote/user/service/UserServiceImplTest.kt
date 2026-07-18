package com.anandashin.volleynote.user.service

import com.anandashin.volleynote.user.LoginInvalidPasswordException
import com.anandashin.volleynote.user.LoginUserNotFoundException
import com.anandashin.volleynote.user.SignUpEmailConflictException
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserServiceImplTest {
    private val userRepository: UserRepository = mock()
    private val service: UserService = UserServiceImpl(userRepository)

    @Test
    fun `createUser - 성공 시 Success 반환하고 저장 호출`() {
        whenever(userRepository.existsByEmail("a@a.com")).thenReturn(false)
        whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

        val result = service.createUser("a@a.com", "Abcd123!", "tester", null)

        assertThat(result).isEqualTo("Success")
        verify(userRepository).save(any<UserEntity>())
    }

    @Test
    fun `createUser - 이메일 중복 시 SignUpEmailConflictException`() {
        whenever(userRepository.existsByEmail("dup@a.com")).thenReturn(true)

        assertThatThrownBy { service.createUser("dup@a.com", "Abcd123!", "x", null) }
            .isInstanceOf(SignUpEmailConflictException::class.java)
    }

    @Test
    fun `login - 사용자 없으면 LoginUserNotFoundException`() {
        whenever(userRepository.findByEmail("nobody@a.com")).thenReturn(null)

        assertThatThrownBy { service.login("nobody@a.com", "whatever") }
            .isInstanceOf(LoginUserNotFoundException::class.java)
    }

    // 재발 방지: BCrypt.checkpw 조건 반전 버그가 있으면 이 테스트는 실패한다.
    // (틀린 비밀번호가 예외를 던지지 않고 그냥 통과하기 때문)
    @Test
    fun `login - 비밀번호 틀리면 LoginInvalidPasswordException`() {
        val hashed = BCrypt.hashpw("correct-pass", BCrypt.gensalt())
        val user = UserEntity(id = 1L, email = "a@a.com", nickname = "a", hashedPassword = hashed)
        whenever(userRepository.findByEmail("a@a.com")).thenReturn(user)

        assertThatThrownBy { service.login("a@a.com", "wrong-pass") }
            .isInstanceOf(LoginInvalidPasswordException::class.java)
    }

    @Test
    fun `login - 성공 시 UserDTO와 비어있지 않은 accessToken 반환`() {
        val password = "Abcd123!"
        val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
        val user = UserEntity(id = 7L, email = "a@a.com", nickname = "tester", hashedPassword = hashed)
        whenever(userRepository.findByEmail("a@a.com")).thenReturn(user)

        val (dto, token) = service.login("a@a.com", password)

        assertThat(dto.id).isEqualTo(7L)
        assertThat(dto.email).isEqualTo("a@a.com")
        assertThat(dto.nickname).isEqualTo("tester")
        assertThat(token).isNotBlank()
    }
}
