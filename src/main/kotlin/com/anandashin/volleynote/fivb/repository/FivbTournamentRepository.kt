package com.anandashin.volleynote.fivb.repository

import com.anandashin.volleynote.fivb.domain.FivbTournament
import org.springframework.data.jpa.repository.JpaRepository

interface FivbTournamentRepository : JpaRepository<FivbTournament, Long> {
    fun findByFivbNo(fivbNo: Long): FivbTournament?

    fun findByGenderAndTypeCodeInOrderByStartDateDesc(
        gender: String,
        typeCodes: Collection<Int>,
    ): List<FivbTournament>

    fun findByGenderAndSeasonAndTypeCodeInOrderByStartDateDesc(
        gender: String,
        season: Int,
        typeCodes: Collection<Int>,
    ): List<FivbTournament>
}
