package com.anandashin.volleynote.fivb.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

// FIVB 미러(B안: Spring 소유·적재·읽기). schema §2.5
@Entity
@Table(name = "fivb_match")
class FivbMatch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "fivb_no")
    val fivbNo: Long = 0,
    @Column(name = "tournament_no")
    val tournamentNo: Long? = null,
    @Column(name = "pool_no")
    val poolNo: Long? = null,
    @Column(name = "pool_name")
    val poolName: String? = null,
    @Column(name = "home_team_no")
    val homeTeamNo: Long? = null,
    @Column(name = "away_team_no")
    val awayTeamNo: Long? = null,
    @Column(name = "home_team_code")
    val homeTeamCode: String? = null,
    @Column(name = "away_team_code")
    val awayTeamCode: String? = null,
    @Column(name = "home_team_name")
    val homeTeamName: String? = null,
    @Column(name = "away_team_name")
    val awayTeamName: String? = null,
    @Column(name = "date_time_utc")
    val dateTimeUtc: LocalDateTime? = null,
    @Column(name = "date_time_local")
    val dateTimeLocal: LocalDateTime? = null,
    @Column(name = "country_code")
    val countryCode: String? = null,
    @Column(name = "city")
    val city: String? = null,
    @Column(name = "hall")
    val hall: String? = null,
    @Column(name = "status_code")
    val statusCode: Int? = null,
    @Column(name = "match_points_a")
    val matchPointsA: Int? = null,
    @Column(name = "match_points_b")
    val matchPointsB: Int? = null,
    @Column(name = "set_scores")
    val setScores: String? = null,
    @Column(name = "duration_total")
    val durationTotal: Int? = null,
    @Column(name = "web_site")
    val webSite: String? = null,
    @Column(name = "synced_at")
    val syncedAt: LocalDateTime? = null,
)
