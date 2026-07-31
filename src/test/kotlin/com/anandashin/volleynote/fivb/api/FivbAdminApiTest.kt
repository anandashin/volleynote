package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.sync.FivbSyncResult
import com.anandashin.volleynote.fivb.sync.FivbSyncService
import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FivbAdminApiTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var fivbSyncService: FivbSyncService

    @Test
    fun `동기화 트리거는 토큰 없으면 401`() {
        mockMvc
            .perform(post("/api/admin/fivb/sync"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `동기화 트리거는 USER 권한이면 403`() {
        authenticate(1L, Role.USER)
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(post("/api/admin/fivb/sync").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `동기화 트리거는 ADMIN 권한이면 200과 결과 요약`() {
        authenticate(2L, Role.ADMIN)
        whenever(fivbSyncService.sync(any())).thenReturn(FivbSyncResult(2025, 3, 40, 12))
        val token = JwtTokenProvider.createJwtToken(2L)

        mockMvc
            .perform(post("/api/admin/fivb/sync?season=2025").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.season").value(2025))
            .andExpect(jsonPath("$.tournaments").value(3))
            .andExpect(jsonPath("$.matches").value(40))
    }

    private fun authenticate(
        userId: Long,
        role: Role,
    ) {
        whenever(userRepository.findById(userId)).thenReturn(
            Optional.of(
                UserEntity(
                    id = userId,
                    email = "u$userId@a.com",
                    nickname = "u$userId",
                    hashedPassword = "irrelevant",
                    role = role,
                ),
            ),
        )
    }
}
