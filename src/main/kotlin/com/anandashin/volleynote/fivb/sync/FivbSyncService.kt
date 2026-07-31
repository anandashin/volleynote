package com.anandashin.volleynote.fivb.sync

import com.anandashin.volleynote.fivb.domain.FivbMatch
import com.anandashin.volleynote.fivb.domain.FivbRanking
import com.anandashin.volleynote.fivb.domain.FivbTournament
import com.anandashin.volleynote.fivb.domain.TournamentType
import com.anandashin.volleynote.fivb.repository.FivbMatchRepository
import com.anandashin.volleynote.fivb.repository.FivbRankingRepository
import com.anandashin.volleynote.fivb.repository.FivbTournamentRepository
import com.anandashin.volleynote.fivb.sync.dto.VisMatch
import com.anandashin.volleynote.fivb.sync.dto.VisPoolRanking
import com.anandashin.volleynote.fivb.sync.dto.VisTournament
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

interface FivbSyncService {
    fun sync(season: Int): FivbSyncResult
}

@Service
open class FivbSyncServiceImpl(
    private val visClient: FivbVisClient,
    private val tournamentRepository: FivbTournamentRepository,
    private val matchRepository: FivbMatchRepository,
    private val rankingRepository: FivbRankingRepository,
) : FivbSyncService {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun sync(season: Int): FivbSyncResult {
        val tournaments =
            visClient
                .getWomenTournaments(season)
                // 국가대표 senior 화이트리스트만 미러에 적재 (schema §4)
                .filter { it.type in TournamentType.NATIONAL_TEAM_SENIOR_CODES }

        var matchCount = 0
        var rankingCount = 0

        for (t in tournaments) {
            upsertTournament(t)

            val matches = visClient.getMatches(t.no)
            matches.forEach { upsertMatch(it, t.no) }
            matchCount += matches.size

            // 순위는 pool 단위 → 경기에서 distinct pool을 모아 pool별 fetch. 에러 pool은 스킵.
            val poolNos = matches.mapNotNull { it.pool?.no }.distinct()
            for (poolNo in poolNos) {
                val entries =
                    runCatching { visClient.getPoolRanking(poolNo) }
                        .getOrElse {
                            log.warn("skip pool ranking (poolNo={}): {}", poolNo, it.message)
                            emptyList()
                        }
                entries.forEach { upsertRanking(it, t.no, poolNo) }
                rankingCount += entries.size
            }
        }

        val result = FivbSyncResult(season, tournaments.size, matchCount, rankingCount)
        log.info("FIVB sync done: {}", result)
        return result
    }

    private fun upsertTournament(vis: VisTournament) {
        val id = tournamentRepository.findByFivbNo(vis.no)?.id ?: 0
        tournamentRepository.save(
            FivbTournament(
                id = id,
                fivbNo = vis.no,
                code = vis.code,
                name = vis.name.orEmpty(),
                typeCode = vis.type,
                statusCode = vis.status,
                gender = vis.gender ?: "W",
                season = vis.season,
                city = vis.city,
                startDate = parseDate(vis.startDate),
                endDate = parseDate(vis.endDate),
                webSite = vis.webSite,
                syncedAt = nowUtc(),
            ),
        )
    }

    private fun upsertMatch(
        vis: VisMatch,
        tournamentNo: Long,
    ) {
        val id = matchRepository.findByFivbNo(vis.no)?.id ?: 0
        matchRepository.save(
            FivbMatch(
                id = id,
                fivbNo = vis.no,
                tournamentNo = tournamentNo,
                poolNo = vis.pool?.no,
                poolName = vis.pool?.name,
                homeTeamNo = vis.teamA?.no,
                awayTeamNo = vis.teamB?.no,
                homeTeamCode = vis.teamA?.code,
                awayTeamCode = vis.teamB?.code,
                homeTeamName = vis.teamAName ?: vis.teamA?.name,
                awayTeamName = vis.teamBName ?: vis.teamB?.name,
                dateTimeUtc = parseUtc(vis.dateTimeUtc),
                dateTimeLocal = parseLocalDateTime(vis.dateTimeLocal),
                countryCode = vis.countryCode,
                city = vis.city,
                hall = vis.hall,
                statusCode = vis.status,
                matchPointsA = vis.matchPointsA,
                matchPointsB = vis.matchPointsB,
                setScores = null,
                durationTotal = vis.durationTotal,
                webSite = vis.webSite,
                syncedAt = nowUtc(),
            ),
        )
    }

    private fun upsertRanking(
        vis: VisPoolRanking,
        tournamentNo: Long,
        poolNo: Long,
    ) {
        val id = rankingRepository.findByFivbNo(vis.no)?.id ?: 0
        rankingRepository.save(
            FivbRanking(
                id = id,
                fivbNo = vis.no,
                tournamentNo = tournamentNo,
                poolNo = poolNo,
                teamCode = vis.teamCode,
                teamName = vis.teamName,
                position = vis.position,
                rank = vis.rank,
                matchesWon = vis.matchesWon,
                matchesLost = vis.matchesLost,
                matchPoints = vis.matchPoints,
                setsWon = vis.setsWon,
                setsLost = vis.setsLost,
                pointsWon = vis.pointsWon,
                pointsLost = vis.pointsLost,
                syncedAt = nowUtc(),
            ),
        )
    }

    private fun nowUtc(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    private fun parseDate(s: String?): LocalDate? =
        s?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    private fun parseLocalDateTime(s: String?): LocalDateTime? =
        s?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDateTime.parse(it) }.getOrNull() }

    // "2025-06-04T03:30:00Z" 같은 오프셋 표기를 UTC LocalDateTime으로. 오프셋 없으면 그대로 파싱.
    private fun parseUtc(s: String?): LocalDateTime? =
        s?.takeIf { it.isNotBlank() }?.let { raw ->
            runCatching { OffsetDateTime.parse(raw).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime() }
                .getOrElse { runCatching { LocalDateTime.parse(raw) }.getOrNull() }
        }
}
