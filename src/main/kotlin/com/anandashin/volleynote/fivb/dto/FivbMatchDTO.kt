package com.anandashin.volleynote.fivb.dto

import com.anandashin.volleynote.fivb.domain.FivbMatch
import com.anandashin.volleynote.fivb.domain.MatchStatus
import java.time.LocalDateTime

data class FivbMatchDTO(
    val fivbNo: Long,
    val tournamentNo: Long?,
    val poolNo: Long?,
    val poolName: String?,
    val homeTeamNo: Long?,
    val awayTeamNo: Long?,
    val homeTeamCode: String?,
    val awayTeamCode: String?,
    val homeTeamName: String?,
    val awayTeamName: String?,
    val dateTimeUtc: LocalDateTime?,
    val dateTimeLocal: LocalDateTime?,
    val countryCode: String?,
    val city: String?,
    val hall: String?,
    val status: MatchStatus,
    val statusCode: Int?,
    val matchPointsA: Int?,
    val matchPointsB: Int?,
    val setScores: String?,
    val durationTotal: Int?,
    val webSite: String?,
) {
    companion object {
        fun from(entity: FivbMatch): FivbMatchDTO =
            FivbMatchDTO(
                fivbNo = entity.fivbNo,
                tournamentNo = entity.tournamentNo,
                poolNo = entity.poolNo,
                poolName = entity.poolName,
                homeTeamNo = entity.homeTeamNo,
                awayTeamNo = entity.awayTeamNo,
                homeTeamCode = entity.homeTeamCode,
                awayTeamCode = entity.awayTeamCode,
                homeTeamName = entity.homeTeamName,
                awayTeamName = entity.awayTeamName,
                dateTimeUtc = entity.dateTimeUtc,
                dateTimeLocal = entity.dateTimeLocal,
                countryCode = entity.countryCode,
                city = entity.city,
                hall = entity.hall,
                status = MatchStatus.fromCode(entity.statusCode),
                statusCode = entity.statusCode,
                matchPointsA = entity.matchPointsA,
                matchPointsB = entity.matchPointsB,
                setScores = entity.setScores,
                durationTotal = entity.durationTotal,
                webSite = entity.webSite,
            )
    }
}
