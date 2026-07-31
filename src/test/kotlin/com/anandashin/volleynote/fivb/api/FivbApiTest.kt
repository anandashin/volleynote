package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.domain.FivbTournament
import com.anandashin.volleynote.fivb.domain.TournamentType
import com.anandashin.volleynote.fivb.repository.FivbMatchRepository
import com.anandashin.volleynote.fivb.repository.FivbRankingRepository
import com.anandashin.volleynote.fivb.repository.FivbTournamentRepository
import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FivbApiTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var tournamentRepository: FivbTournamentRepository

    @MockitoBean
    private lateinit var matchRepository: FivbMatchRepository

    @MockitoBean
    private lateinit var rankingRepository: FivbRankingRepository

    @Test
    fun `대회 목록은 토큰 없으면 401`() {
        mockMvc
            .perform(get("/api/fivb/tournaments"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `대회 목록은 유효 토큰이면 200과 국가대표 대회 반환`() {
        authenticate(1L)
        whenever(
            tournamentRepository.findByGenderAndTypeCodeInOrderByStartDateDesc(
                "W",
                TournamentType.NATIONAL_TEAM_SENIOR_CODES,
            ),
        ).thenReturn(
            listOf(
                FivbTournament(id = 1543, fivbNo = 1543, typeCode = 12, name = "Women's VNL 2025", gender = "W"),
            ),
        )
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(get("/api/fivb/tournaments").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].fivbNo").value(1543))
            .andExpect(jsonPath("$[0].type").value("NATIONS_LEAGUE"))
    }

    @Test
    fun `없는 대회 상세는 404 + TOURNAMENT_NOT_FOUND`() {
        authenticate(1L)
        whenever(tournamentRepository.findByFivbNo(999)).thenReturn(null)
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(get("/api/fivb/tournaments/999").header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("TOURNAMENT_NOT_FOUND"))
    }

    private fun authenticate(userId: Long) {
        whenever(userRepository.findById(userId)).thenReturn(
            Optional.of(
                UserEntity(
                    id = userId,
                    email = "u$userId@a.com",
                    nickname = "u$userId",
                    hashedPassword = "irrelevant",
                    role = Role.USER,
                ),
            ),
        )
    }
}
