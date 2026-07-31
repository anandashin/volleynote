package com.anandashin.volleynote.fivb.sync.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// FIVB VIS JSON 응답 공통 봉투: { data: [...], nbItems, version }
@JsonIgnoreProperties(ignoreUnknown = true)
data class VisListResponse<T>(
    val data: List<T> = emptyList(),
    val nbItems: Int = 0,
    val version: Long = 0,
)

// GetVolleyTournamentList 항목
@JsonIgnoreProperties(ignoreUnknown = true)
data class VisTournament(
    val no: Long = 0,
    val type: Int? = null,
    val status: Int? = null,
    val gender: String? = null,
    val season: Int? = null,
    val code: String? = null,
    val name: String? = null,
    val city: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val webSite: String? = null,
)

// GetVolleyMatchList 항목 (Pool/TeamA/TeamB relation 포함)
@JsonIgnoreProperties(ignoreUnknown = true)
data class VisMatch(
    val no: Long = 0,
    val status: Int? = null,
    val dateTimeUtc: String? = null,
    val dateTimeLocal: String? = null,
    val countryCode: String? = null,
    val city: String? = null,
    val hall: String? = null,
    val teamAName: String? = null,
    val teamBName: String? = null,
    val matchPointsA: Int? = null,
    val matchPointsB: Int? = null,
    val durationTotal: Int? = null,
    val webSite: String? = null,
    val pool: VisRef? = null,
    val teamA: VisRef? = null,
    val teamB: VisRef? = null,
)

// no/code/name만 갖는 relation 참조 (Pool, TeamA, TeamB 공용)
@JsonIgnoreProperties(ignoreUnknown = true)
data class VisRef(
    val no: Long? = null,
    val code: String? = null,
    val name: String? = null,
)

// GetVolleyPoolRanking 항목
@JsonIgnoreProperties(ignoreUnknown = true)
data class VisPoolRanking(
    val no: Long = 0,
    val position: Int? = null,
    val rank: Int? = null,
    val teamCode: String? = null,
    val teamName: String? = null,
    val matchesWon: Int? = null,
    val matchesLost: Int? = null,
    val matchPoints: Int? = null,
    val setsWon: Int? = null,
    val setsLost: Int? = null,
    val pointsWon: Int? = null,
    val pointsLost: Int? = null,
)
