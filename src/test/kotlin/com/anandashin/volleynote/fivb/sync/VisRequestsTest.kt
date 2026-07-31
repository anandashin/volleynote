package com.anandashin.volleynote.fivb.sync

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VisRequestsTest {
    @Test
    fun `womenTournaments - 성별·시즌 필터와 요청 타입 포함`() {
        val xml = VisRequests.womenTournaments(2025)

        assertThat(xml).contains("""Type="GetVolleyTournamentList"""")
        assertThat(xml).contains("Genders='W'")
        assertThat(xml).contains("Seasons='2025'")
        assertThat(xml).contains("Season") // 시즌 확보용 Fields
    }

    @Test
    fun `tournamentMatches - 대회 필터와 Pool·TeamA·TeamB relation 포함`() {
        val xml = VisRequests.tournamentMatches(1543)

        assertThat(xml).contains("""Type="GetVolleyMatchList"""")
        assertThat(xml).contains("NoTournament='1543'")
        assertThat(xml).contains("Name='Pool'")
        assertThat(xml).contains("Name='TeamA'")
        assertThat(xml).contains("Name='TeamB'")
        assertThat(xml).contains("DateTimeUtc")
    }

    @Test
    fun `poolRanking - NoPool과 승패·세트·승점 Fields 포함`() {
        val xml = VisRequests.poolRanking(4990)

        assertThat(xml).contains("""Type="GetVolleyPoolRanking"""")
        assertThat(xml).contains("""NoPool="4990"""")
        assertThat(xml).contains("MatchPoints")
        assertThat(xml).contains("SetsWon")
    }
}
