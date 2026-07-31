package com.anandashin.volleynote.fivb.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.time.LocalDateTime

// FIVB 미러 풀 순위(읽기 전용). schema §2.6 (GetVolleyPoolRanking 기반)
@Entity
@Immutable
@Table(name = "fivb_ranking")
class FivbRanking(
    @Id
    val id: Long = 0,
    @Column(name = "fivb_no")
    val fivbNo: Long = 0,
    @Column(name = "tournament_no")
    val tournamentNo: Long? = null,
    @Column(name = "pool_no")
    val poolNo: Long = 0,
    @Column(name = "team_code")
    val teamCode: String? = null,
    @Column(name = "team_name")
    val teamName: String? = null,
    @Column(name = "position")
    val position: Int? = null,
    @Column(name = "rank")
    val rank: Int? = null,
    @Column(name = "matches_won")
    val matchesWon: Int? = null,
    @Column(name = "matches_lost")
    val matchesLost: Int? = null,
    @Column(name = "match_points")
    val matchPoints: Int? = null,
    @Column(name = "sets_won")
    val setsWon: Int? = null,
    @Column(name = "sets_lost")
    val setsLost: Int? = null,
    @Column(name = "points_won")
    val pointsWon: Int? = null,
    @Column(name = "points_lost")
    val pointsLost: Int? = null,
    @Column(name = "synced_at")
    val syncedAt: LocalDateTime? = null,
)
