package com.anandashin.volleynote.fivb.sync

import com.anandashin.volleynote.fivb.domain.FivbMatch
import com.anandashin.volleynote.fivb.domain.FivbTournament
import com.anandashin.volleynote.fivb.repository.FivbMatchRepository
import com.anandashin.volleynote.fivb.repository.FivbRankingRepository
import com.anandashin.volleynote.fivb.repository.FivbTournamentRepository
import com.anandashin.volleynote.fivb.sync.dto.VisMatch
import com.anandashin.volleynote.fivb.sync.dto.VisPoolRanking
import com.anandashin.volleynote.fivb.sync.dto.VisRef
import com.anandashin.volleynote.fivb.sync.dto.VisTournament
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.Month

class FivbSyncServiceImplTest {
    private val visClient: FivbVisClient = mock()
    private val tournamentRepository: FivbTournamentRepository = mock()
    private val matchRepository: FivbMatchRepository = mock()
    private val rankingRepository: FivbRankingRepository = mock()
    private val service: FivbSyncService =
        FivbSyncServiceImpl(visClient, tournamentRepository, matchRepository, rankingRepository)

    @Test
    fun `sync - 국가대표 화이트리스트만 적재하고 클럽·유스는 스킵`() {
        // type 12(VNL)=국가대표, type 14(클럽 세계선수권)=제외 대상
        whenever(visClient.getWomenTournaments(2025)).thenReturn(
            listOf(
                visTournament(no = 1543, type = 12),
                visTournament(no = 9999, type = 14),
            ),
        )
        whenever(visClient.getMatches(any())).thenReturn(emptyList())

        val result = service.sync(2025)

        // 국가대표 1개만 카운트 + 저장
        assertThat(result.tournaments).isEqualTo(1)
        val captor = argumentCaptor<FivbTournament>()
        verify(tournamentRepository).save(captor.capture())
        assertThat(captor.firstValue.fivbNo).isEqualTo(1543)
        // 클럽 대회(9999)의 경기는 조회조차 안 함
        verify(visClient, never()).getMatches(9999)
    }

    @Test
    fun `sync - 경기 upsert 시 UTC 파싱과 팀 relation 매핑`() {
        whenever(visClient.getWomenTournaments(2025)).thenReturn(listOf(visTournament(no = 1543, type = 12)))
        whenever(visClient.getMatches(1543)).thenReturn(
            listOf(
                VisMatch(
                    no = 21553,
                    status = 25,
                    dateTimeUtc = "2025-06-04T03:30:00Z",
                    dateTimeLocal = "2025-06-04T11:30:00",
                    teamAName = "France",
                    teamBName = "Türkiye",
                    matchPointsA = 1,
                    matchPointsB = 3,
                    pool = VisRef(no = 4990, name = "Pool 3"),
                    teamA = VisRef(no = 7542, code = "FRA", name = "France"),
                    teamB = VisRef(no = 7551, code = "TUR", name = "Türkiye"),
                ),
            ),
        )
        whenever(visClient.getPoolRanking(4990)).thenReturn(emptyList())

        val result = service.sync(2025)

        assertThat(result.matches).isEqualTo(1)
        val captor = argumentCaptor<FivbMatch>()
        verify(matchRepository).save(captor.capture())
        val saved = captor.firstValue
        assertThat(saved.fivbNo).isEqualTo(21553)
        assertThat(saved.tournamentNo).isEqualTo(1543)
        assertThat(saved.homeTeamNo).isEqualTo(7542)
        assertThat(saved.awayTeamCode).isEqualTo("TUR")
        assertThat(saved.poolNo).isEqualTo(4990)
        // "2025-06-04T03:30:00Z" → UTC LocalDateTime 03:30
        assertThat(saved.dateTimeUtc).isEqualTo(LocalDateTime.of(2025, Month.JUNE, 4, 3, 30, 0))
    }

    @Test
    fun `sync - 경기의 pool에서 순위를 조회해 적재`() {
        whenever(visClient.getWomenTournaments(2025)).thenReturn(listOf(visTournament(no = 1543, type = 12)))
        whenever(visClient.getMatches(1543)).thenReturn(
            listOf(
                VisMatch(no = 1, pool = VisRef(no = 4990)),
                VisMatch(no = 2, pool = VisRef(no = 4990)), // 같은 pool → 한 번만 순위 조회
            ),
        )
        whenever(visClient.getPoolRanking(4990)).thenReturn(
            listOf(VisPoolRanking(no = 111, rank = 4, teamCode = "KOR", matchPoints = 5)),
        )

        val result = service.sync(2025)

        assertThat(result.rankings).isEqualTo(1)
        verify(visClient).getPoolRanking(4990) // distinct pool
        verify(rankingRepository).save(any())
    }

    @Test
    fun `sync - 순위 조회 실패한 pool은 스킵하고 계속 진행`() {
        whenever(visClient.getWomenTournaments(2025)).thenReturn(listOf(visTournament(no = 1543, type = 12)))
        whenever(visClient.getMatches(1543)).thenReturn(listOf(VisMatch(no = 1, pool = VisRef(no = 8000))))
        whenever(visClient.getPoolRanking(8000)).thenThrow(RuntimeException("NotARankingPool"))

        val result = service.sync(2025)

        assertThat(result.rankings).isEqualTo(0)
        verify(rankingRepository, never()).save(any())
    }

    @Test
    fun `sync - 기존 대회는 id를 보존해 upsert(update)`() {
        whenever(visClient.getWomenTournaments(2025)).thenReturn(listOf(visTournament(no = 1543, type = 12)))
        whenever(visClient.getMatches(any())).thenReturn(emptyList())
        // 이미 존재하는 대회 (id=77)
        whenever(tournamentRepository.findByFivbNo(1543))
            .thenReturn(FivbTournament(id = 77, fivbNo = 1543, name = "old"))

        service.sync(2025)

        val captor = argumentCaptor<FivbTournament>()
        verify(tournamentRepository).save(captor.capture())
        assertThat(captor.firstValue.id).isEqualTo(77) // insert가 아니라 update
    }

    private fun visTournament(
        no: Long,
        type: Int,
    ) = VisTournament(no = no, type = type, name = "T$no", startDate = "2025-06-04", endDate = "2025-07-27")
}
