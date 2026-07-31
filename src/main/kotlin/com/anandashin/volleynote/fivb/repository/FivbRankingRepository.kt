package com.anandashin.volleynote.fivb.repository

import com.anandashin.volleynote.fivb.domain.FivbRanking
import org.springframework.data.jpa.repository.JpaRepository

interface FivbRankingRepository : JpaRepository<FivbRanking, Long> {
    fun findByTournamentNoOrderByPoolNoAscPositionAsc(tournamentNo: Long): List<FivbRanking>
}
