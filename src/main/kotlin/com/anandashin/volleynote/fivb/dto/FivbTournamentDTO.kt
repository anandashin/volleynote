package com.anandashin.volleynote.fivb.dto

import com.anandashin.volleynote.fivb.domain.FivbTournament
import com.anandashin.volleynote.fivb.domain.TournamentType
import java.time.LocalDate

data class FivbTournamentDTO(
    val fivbNo: Long,
    val code: String?,
    val name: String,
    val type: TournamentType,
    val typeCode: Int?,
    val gender: String?,
    val season: Int?,
    val city: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val webSite: String?,
) {
    companion object {
        fun from(entity: FivbTournament): FivbTournamentDTO =
            FivbTournamentDTO(
                fivbNo = entity.fivbNo,
                code = entity.code,
                name = entity.name,
                type = TournamentType.fromCode(entity.typeCode),
                typeCode = entity.typeCode,
                gender = entity.gender,
                season = entity.season,
                city = entity.city,
                startDate = entity.startDate,
                endDate = entity.endDate,
                webSite = entity.webSite,
            )
    }
}
