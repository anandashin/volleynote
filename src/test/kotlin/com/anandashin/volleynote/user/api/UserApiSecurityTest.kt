package com.anandashin.volleynote.user.api

import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserApiSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    // --- signup / login (permitAll) ---

    @Test
    fun `signup 경로는 익명 접근 허용 - permitAll 통과 후 validation 단계에서 400`() {
        mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"),
        ).andExpect(status().isBadRequest)
    }

    // --- /me GET ---

    @Test
    fun `me GET은 토큰 없이 접근 시 401 - JwtAuthenticationEntryPoint의 JSON 응답`() {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `me GET은 잘못된 토큰이면 401`() {
        mockMvc.perform(
            get("/api/users/me").header("Authorization", "Bearer invalid.token.value"),
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `me GET은 유효한 토큰이면 200과 사용자 정보 반환`() {
        val user =
            UserEntity(
                id = 42L,
                email = "a@a.com",
                nickname = "tester",
                hashedPassword = "irrelevant",
                introduction = "hi",
                role = Role.USER,
            )
        whenever(userRepository.findById(42L)).thenReturn(Optional.of(user))
        val token = JwtTokenProvider.createJwtToken(42L)

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.email").value("a@a.com"))
            .andExpect(jsonPath("$.nickname").value("tester"))
            .andExpect(jsonPath("$.role").value("USER"))
    }

    // --- /me PATCH ---

    @Test
    fun `me PATCH는 토큰 없으면 401`() {
        mockMvc.perform(
            patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nickname":"newname"}"""),
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `me PATCH는 nickname 길이 위반 시 400`() {
        val user = userEntity(id = 1L, role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(
            patch("/api/users/me")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nickname":"this-nickname-is-way-too-long-over-16"}"""),
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `me PATCH는 유효 토큰과 정상 body면 200과 갱신된 UserDTO`() {
        val user = userEntity(id = 1L, nickname = "old", introduction = "old-intro", role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(
            patch("/api/users/me")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nickname":"new","introduction":"new-intro"}"""),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nickname").value("new"))
            .andExpect(jsonPath("$.introduction").value("new-intro"))
    }

    // --- /me DELETE ---

    @Test
    fun `me DELETE는 토큰 없으면 401`() {
        mockMvc.perform(delete("/api/users/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `me DELETE는 유효 토큰이면 204 No Content 및 repo delete 호출`() {
        val user = userEntity(id = 1L, role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(delete("/api/users/me").header("Authorization", "Bearer $token"))
            .andExpect(status().isNoContent)

        verify(userRepository).delete(any<UserEntity>())
    }

    // --- GET /users/{userId} (공개 프로필) ---

    @Test
    fun `공개 프로필 조회는 인증 필요 - 익명 401`() {
        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `공개 프로필 조회는 200과 PublicUserDTO - email 미노출`() {
        val me = userEntity(id = 1L, role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(me))
        val target =
            userEntity(
                id = 99L,
                email = "target@a.com",
                nickname = "targetuser",
                introduction = "hello",
                role = Role.USER,
            )
        whenever(userRepository.findById(99L)).thenReturn(Optional.of(target))
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(get("/api/users/99").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.nickname").value("targetuser"))
            .andExpect(jsonPath("$.introduction").value("hello"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.email").doesNotExist())
    }

    @Test
    fun `공개 프로필 조회 - 없는 사용자면 404`() {
        val me = userEntity(id = 1L, role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(me))
        whenever(userRepository.findById(9999L)).thenReturn(Optional.empty())
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(get("/api/users/9999").header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)
    }

    // --- 관리자 경로 ---

    @Test
    fun `admin 경로는 익명이면 401`() {
        mockMvc.perform(get("/api/admin/anything"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `admin 경로는 USER 권한이면 403 - JwtAccessDeniedHandler`() {
        val user = userEntity(id = 1L, role = Role.USER)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc.perform(
            get("/api/admin/anything").header("Authorization", "Bearer $token"),
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value(403))
    }

    @Test
    fun `admin 경로는 ADMIN 권한이면 인가 통과 - 엔드포인트 없어 404`() {
        val admin = userEntity(id = 2L, role = Role.ADMIN)
        whenever(userRepository.findById(2L)).thenReturn(Optional.of(admin))
        val token = JwtTokenProvider.createJwtToken(2L)

        mockMvc.perform(
            get("/api/admin/anything").header("Authorization", "Bearer $token"),
        ).andExpect(status().isNotFound)
    }

    private fun userEntity(
        id: Long,
        email: String = "u$id@a.com",
        nickname: String = "u$id",
        introduction: String? = null,
        role: Role = Role.USER,
    ) = UserEntity(
        id = id,
        email = email,
        nickname = nickname,
        hashedPassword = "irrelevant",
        introduction = introduction,
        role = role,
    )
}
