package com.anandashin.volleynote.fivb.service

import com.anandashin.volleynote.fivb.MatchNotFoundException
import com.anandashin.volleynote.fivb.TournamentNotFoundException
import com.anandashin.volleynote.fivb.domain.TournamentType
import com.anandashin.volleynote.fivb.dto.FivbMatchDTO
import com.anandashin.volleynote.fivb.dto.FivbRankingDTO
import com.anandashin.volleynote.fivb.dto.FivbTournamentDTO
import com.anandashin.volleynote.fivb.repository.FivbMatchRepository
import com.anandashin.volleynote.fivb.repository.FivbRankingRepository
import com.anandashin.volleynote.fivb.repository.FivbTournamentRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

interface FivbService {
    fun getTournaments(season: Int?): List<FivbTournamentDTO>

    fun getTournament(fivbNo: Long): FivbTournamentDTO

    fun getMatches(tournamentFivbNo: Long): List<FivbMatchDTO>

    fun getMatch(fivbNo: Long): FivbMatchDTO

    fun getRankings(tournamentFivbNo: Long): List<FivbRankingDTO>
}

@Service
open class FivbServiceImpl(
    private val tournamentRepository: FivbTournamentRepository,
    private val matchRepository: FivbMatchRepository,
    private val rankingRepository: FivbRankingRepository,
) : FivbService {
    // 여자 국가대표만. 미러는 이미 women-only지만 방어적으로 gender='W' + type 화이트리스트 필터.
    private val gender = "W"
    private val nationalTeamTypes = TournamentType.NATIONAL_TEAM_SENIOR_CODES

    @Cacheable("fivbTournaments")
    override fun getTournaments(season: Int?): List<FivbTournamentDTO> {
        val entities =
            if (season == null) {
                tournamentRepository.findByGenderAndTypeCodeInOrderByStartDateDesc(gender, nationalTeamTypes)
            } else {
                tournamentRepository.findByGenderAndSeasonAndTypeCodeInOrderByStartDateDesc(gender, season, nationalTeamTypes)
            }
        return entities.map(FivbTournamentDTO::from)
    }

    @Cacheable("fivbTournament")
    override fun getTournament(fivbNo: Long): FivbTournamentDTO {
        val tournament = tournamentRepository.findByFivbNo(fivbNo) ?: throw TournamentNotFoundException()
        return FivbTournamentDTO.from(tournament)
    }

    @Cacheable("fivbMatches")
    override fun getMatches(tournamentFivbNo: Long): List<FivbMatchDTO> {
        // 대회 존재 확인 (없으면 404)
        if (tournamentRepository.findByFivbNo(tournamentFivbNo) == null) {
            throw TournamentNotFoundException()
        }
        return matchRepository
            .findByTournamentNoOrderByDateTimeUtcAsc(tournamentFivbNo)
            .map(FivbMatchDTO::from)
    }

    @Cacheable("fivbMatch")
    override fun getMatch(fivbNo: Long): FivbMatchDTO {
        val match = matchRepository.findByFivbNo(fivbNo) ?: throw MatchNotFoundException()
        return FivbMatchDTO.from(match)
    }

    @Cacheable("fivbRankings")
    override fun getRankings(tournamentFivbNo: Long): List<FivbRankingDTO> {
        if (tournamentRepository.findByFivbNo(tournamentFivbNo) == null) {
            throw TournamentNotFoundException()
        }
        return rankingRepository
            .findByTournamentNoOrderByPoolNoAscPositionAsc(tournamentFivbNo)
            .map(FivbRankingDTO::from)
    }
}
