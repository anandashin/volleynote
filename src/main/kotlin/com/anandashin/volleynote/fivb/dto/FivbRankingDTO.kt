package com.anandashin.volleynote.fivb.dto

import com.anandashin.volleynote.fivb.domain.FivbRanking

data class FivbRankingDTO(
    val tournamentNo: Long?,
    val poolNo: Long,
    val teamCode: String?,
    val teamName: String?,
    val position: Int?,
    val rank: Int?,
    val matchesWon: Int?,
    val matchesLost: Int?,
    val matchPoints: Int?,
    val setsWon: Int?,
    val setsLost: Int?,
    val pointsWon: Int?,
    val pointsLost: Int?,
) {
    companion object {
        fun from(entity: FivbRanking): FivbRankingDTO =
            FivbRankingDTO(
                tournamentNo = entity.tournamentNo,
                poolNo = entity.poolNo,
                teamCode = entity.teamCode,
                teamName = entity.teamName,
                position = entity.position,
                rank = entity.rank,
                matchesWon = entity.matchesWon,
                matchesLost = entity.matchesLost,
                matchPoints = entity.matchPoints,
                setsWon = entity.setsWon,
                setsLost = entity.setsLost,
                pointsWon = entity.pointsWon,
                pointsLost = entity.pointsLost,
            )
    }
}
