package com.anandashin.volleynote.user.api

import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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

    @Test
    fun `signup 경로는 익명 접근 허용 - permitAll 통과 후 validation 단계에서 400`() {
        mockMvc.perform(
            post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"),
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `me 경로는 토큰 없이 접근 시 401 - JwtAuthenticationEntryPoint의 JSON 응답`() {
        mockMvc.perform(get("/api/user/me"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `me 경로는 잘못된 토큰이면 401`() {
        mockMvc.perform(
            get("/api/user/me").header("Authorization", "Bearer invalid.token.value"),
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `me 경로는 유효한 토큰이면 200과 사용자 정보 반환`() {
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

        mockMvc.perform(get("/api/user/me").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.email").value("a@a.com"))
            .andExpect(jsonPath("$.nickname").value("tester"))
            .andExpect(jsonPath("$.role").value("USER"))
    }

    @Test
    fun `admin 경로는 익명이면 401`() {
        mockMvc.perform(get("/api/admin/anything"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `admin 경로는 USER 권한이면 403 - JwtAccessDeniedHandler`() {
        val user =
            UserEntity(
                id = 1L,
                email = "u@u.com",
                nickname = "u",
                hashedPassword = "x",
                role = Role.USER,
            )
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
        val admin =
            UserEntity(
                id = 2L,
                email = "admin@a.com",
                nickname = "admin",
                hashedPassword = "x",
                role = Role.ADMIN,
            )
        whenever(userRepository.findById(2L)).thenReturn(Optional.of(admin))
        val token = JwtTokenProvider.createJwtToken(2L)

        mockMvc.perform(
            get("/api/admin/anything").header("Authorization", "Bearer $token"),
        ).andExpect(status().isNotFound)
    }
}
