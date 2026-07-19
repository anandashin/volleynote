package com.anandashin.volleynote.user.service

import com.anandashin.volleynote.user.LoginInvalidPasswordException
import com.anandashin.volleynote.user.LoginUserNotFoundException
import com.anandashin.volleynote.user.SignUpEmailConflictException
import com.anandashin.volleynote.user.UserNotFoundException
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.dto.UpdateMeRequest
import com.anandashin.volleynote.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class UserServiceImplTest {
    private val userRepository: UserRepository = mock()
    private val service: UserService = UserServiceImpl(userRepository)

    @Test
    fun `createUser - 성공 시 success 반환하고 저장 호출`() {
        whenever(userRepository.existsByEmail("a@a.com")).thenReturn(false)
        whenever(userRepository.save(any<UserEntity>())).thenAnswer { it.arguments[0] }

        val result = service.createUser("a@a.com", "Abcd123!", "tester", null)

        assertThat(result).isEqualTo("success")
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

    @Test
    fun `updateMe - 제공된 필드만 변경 (nickname 있음, introduction 없음)`() {
        val user =
            UserEntity(
                id = 1L,
                email = "a@a.com",
                nickname = "old",
                hashedPassword = "x",
                introduction = "keep-me",
            )
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = service.updateMe(1L, UpdateMeRequest(nickname = "new"))

        assertThat(result.nickname).isEqualTo("new")
        assertThat(result.introduction).isEqualTo("keep-me")
    }

    @Test
    fun `updateMe - 사용자 없으면 UserNotFoundException`() {
        whenever(userRepository.findById(999L)).thenReturn(Optional.empty())

        assertThatThrownBy { service.updateMe(999L, UpdateMeRequest(nickname = "x")) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `deleteMe - 사용자 로드 후 repo delete 호출`() {
        val user = UserEntity(id = 1L, email = "a@a.com", nickname = "a", hashedPassword = "x")
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))

        service.deleteMe(1L)

        verify(userRepository).delete(user)
    }

    @Test
    fun `deleteMe - 사용자 없으면 UserNotFoundException`() {
        whenever(userRepository.findById(999L)).thenReturn(Optional.empty())

        assertThatThrownBy { service.deleteMe(999L) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `getPublicProfile - email 제외한 필드만 반환`() {
        val user =
            UserEntity(
                id = 5L,
                email = "target@a.com",
                nickname = "targetuser",
                hashedPassword = "x",
                introduction = "hi",
                role = Role.USER,
            )
        whenever(userRepository.findById(5L)).thenReturn(Optional.of(user))

        val dto = service.getPublicProfile(5L)

        assertThat(dto.id).isEqualTo(5L)
        assertThat(dto.nickname).isEqualTo("targetuser")
        assertThat(dto.introduction).isEqualTo("hi")
        assertThat(dto.role).isEqualTo(Role.USER)
        // PublicUserDTO 타입 자체가 email 필드를 갖지 않도록 정의됨 (컴파일 시 보장)
    }

    @Test
    fun `getPublicProfile - 없으면 UserNotFoundException`() {
        whenever(userRepository.findById(9999L)).thenReturn(Optional.empty())

        assertThatThrownBy { service.getPublicProfile(9999L) }
            .isInstanceOf(UserNotFoundException::class.java)
    }
}
