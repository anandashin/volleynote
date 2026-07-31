package com.anandashin.volleynote.fivb.repository

import com.anandashin.volleynote.fivb.domain.FivbMatch
import org.springframework.data.jpa.repository.JpaRepository

interface FivbMatchRepository : JpaRepository<FivbMatch, Long> {
    fun findByFivbNo(fivbNo: Long): FivbMatch?

    fun findByTournamentNoOrderByDateTimeUtcAsc(tournamentNo: Long): List<FivbMatch>
}
