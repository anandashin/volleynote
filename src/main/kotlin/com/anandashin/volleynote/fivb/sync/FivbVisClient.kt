package com.anandashin.volleynote.fivb.sync

import com.anandashin.volleynote.fivb.sync.dto.VisListResponse
import com.anandashin.volleynote.fivb.sync.dto.VisMatch
import com.anandashin.volleynote.fivb.sync.dto.VisPoolRanking
import com.anandashin.volleynote.fivb.sync.dto.VisTournament
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

// FIVB VIS 웹서비스 호출 래퍼. XML 요청 → JSON 응답 → DTO 파싱.
@Component
class FivbVisClient(
    private val fivbRestClient: RestClient,
) {
    // 제네릭 응답 타입은 익명 ParameterizedTypeReference로 명시(리파이드 인라인은 시그니처 유실 위험).
    private val tournamentListType = object : ParameterizedTypeReference<VisListResponse<VisTournament>>() {}
    private val matchListType = object : ParameterizedTypeReference<VisListResponse<VisMatch>>() {}
    private val rankingListType = object : ParameterizedTypeReference<VisListResponse<VisPoolRanking>>() {}

    fun getWomenTournaments(season: Int): List<VisTournament> =
        get(VisRequests.womenTournaments(season), tournamentListType)

    fun getMatches(tournamentNo: Long): List<VisMatch> =
        get(VisRequests.tournamentMatches(tournamentNo), matchListType)

    fun getPoolRanking(poolNo: Long): List<VisPoolRanking> =
        get(VisRequests.poolRanking(poolNo), rankingListType)

    private fun <T> get(
        request: String,
        type: ParameterizedTypeReference<VisListResponse<T>>,
    ): List<T> {
        val response =
            fivbRestClient
                .get()
                .uri { it.queryParam("Request", request).build() }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(type)
        return response?.data ?: emptyList()
    }
}
