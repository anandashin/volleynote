package com.anandashin.volleynote.fivb.service

import com.anandashin.volleynote.fivb.MatchNotFoundException
import com.anandashin.volleynote.fivb.TournamentNotFoundException
import com.anandashin.volleynote.fivb.domain.FivbMatch
import com.anandashin.volleynote.fivb.domain.FivbRanking
import com.anandashin.volleynote.fivb.domain.FivbTournament
import com.anandashin.volleynote.fivb.domain.MatchStatus
import com.anandashin.volleynote.fivb.domain.TournamentType
import com.anandashin.volleynote.fivb.repository.FivbMatchRepository
import com.anandashin.volleynote.fivb.repository.FivbRankingRepository
import com.anandashin.volleynote.fivb.repository.FivbTournamentRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FivbServiceImplTest {
    private val tournamentRepository: FivbTournamentRepository = mock()
    private val matchRepository: FivbMatchRepository = mock()
    private val rankingRepository: FivbRankingRepository = mock()
    private val service: FivbService = FivbServiceImpl(tournamentRepository, matchRepository, rankingRepository)

    private val womenTypes = TournamentType.NATIONAL_TEAM_SENIOR_CODES

    @Test
    fun `getTournaments - season 없으면 성별·타입 화이트리스트로 조회하고 type enum 매핑`() {
        whenever(tournamentRepository.findByGenderAndTypeCodeInOrderByStartDateDesc("W", womenTypes))
            .thenReturn(listOf(tournament(fivbNo = 1543, typeCode = 12, name = "VNL 2025")))

        val result = service.getTournaments(season = null)

        assertThat(result).hasSize(1)
        assertThat(result[0].fivbNo).isEqualTo(1543)
        assertThat(result[0].type).isEqualTo(TournamentType.NATIONS_LEAGUE)
    }

    @Test
    fun `getTournaments - season 있으면 season 조합 쿼리 사용`() {
        whenever(tournamentRepository.findByGenderAndSeasonAndTypeCodeInOrderByStartDateDesc("W", 2025, womenTypes))
            .thenReturn(listOf(tournament(fivbNo = 1521, typeCode = 3, season = 2025)))

        val result = service.getTournaments(season = 2025)

        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo(TournamentType.WORLD_CHAMPIONSHIP)
        assertThat(result[0].season).isEqualTo(2025)
    }

    @Test
    fun `getTournament - 존재하면 DTO 반환`() {
        whenever(tournamentRepository.findByFivbNo(1543)).thenReturn(tournament(fivbNo = 1543, name = "VNL"))

        val dto = service.getTournament(1543)

        assertThat(dto.fivbNo).isEqualTo(1543)
        assertThat(dto.name).isEqualTo("VNL")
    }

    @Test
    fun `getTournament - 없으면 TournamentNotFoundException`() {
        whenever(tournamentRepository.findByFivbNo(999)).thenReturn(null)

        assertThatThrownBy { service.getTournament(999) }
            .isInstanceOf(TournamentNotFoundException::class.java)
    }

    @Test
    fun `getMatches - 대회 있으면 경기 목록을 dateTimeUtc 순으로 매핑`() {
        whenever(tournamentRepository.findByFivbNo(1543)).thenReturn(tournament(fivbNo = 1543))
        whenever(matchRepository.findByTournamentNoOrderByDateTimeUtcAsc(1543))
            .thenReturn(listOf(match(fivbNo = 21553, statusCode = 25)))

        val result = service.getMatches(1543)

        assertThat(result).hasSize(1)
        assertThat(result[0].fivbNo).isEqualTo(21553)
        assertThat(result[0].status).isEqualTo(MatchStatus.FINISHED)
    }

    @Test
    fun `getMatches - 대회 없으면 TournamentNotFoundException`() {
        whenever(tournamentRepository.findByFivbNo(999)).thenReturn(null)

        assertThatThrownBy { service.getMatches(999) }
            .isInstanceOf(TournamentNotFoundException::class.java)
    }

    @Test
    fun `getMatch - 없으면 MatchNotFoundException`() {
        whenever(matchRepository.findByFivbNo(404)).thenReturn(null)

        assertThatThrownBy { service.getMatch(404) }
            .isInstanceOf(MatchNotFoundException::class.java)
    }

    @Test
    fun `getRankings - 대회 있으면 순위 목록 매핑`() {
        whenever(tournamentRepository.findByFivbNo(1543)).thenReturn(tournament(fivbNo = 1543))
        whenever(rankingRepository.findByTournamentNoOrderByPoolNoAscPositionAsc(1543))
            .thenReturn(listOf(ranking(teamCode = "KOR", rank = 4, matchPoints = 5)))

        val result = service.getRankings(1543)

        assertThat(result).hasSize(1)
        assertThat(result[0].teamCode).isEqualTo("KOR")
        assertThat(result[0].matchPoints).isEqualTo(5)
    }

    @Test
    fun `getRankings - 대회 없으면 TournamentNotFoundException`() {
        whenever(tournamentRepository.findByFivbNo(999)).thenReturn(null)

        assertThatThrownBy { service.getRankings(999) }
            .isInstanceOf(TournamentNotFoundException::class.java)
    }

    private fun tournament(
        fivbNo: Long = 1L,
        typeCode: Int = 12,
        name: String = "t",
        season: Int? = null,
    ) = FivbTournament(id = fivbNo, fivbNo = fivbNo, typeCode = typeCode, name = name, gender = "W", season = season)

    private fun match(
        fivbNo: Long = 1L,
        statusCode: Int = 25,
    ) = FivbMatch(id = fivbNo, fivbNo = fivbNo, tournamentNo = 1543, statusCode = statusCode)

    private fun ranking(
        teamCode: String = "KOR",
        rank: Int = 1,
        matchPoints: Int = 0,
    ) = FivbRanking(id = 1L, fivbNo = 1L, tournamentNo = 1543, poolNo = 1L, teamCode = teamCode, rank = rank, matchPoints = matchPoints)
}
