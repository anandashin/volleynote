package com.anandashin.volleynote.fivb.sync

// FIVB VIS XML 요청 문자열 빌더 (순수 함수 — 단위 테스트 대상).
// 참고: fivbvis_client tests/*.py 의 Fields/Filter/Relation 구성.
object VisRequests {
    // 여자 대회 목록 (특정 시즌). type 화이트리스트는 응답 후 sync 레이어에서 필터.
    fun womenTournaments(season: Int): String =
        """<Request Type="GetVolleyTournamentList" """ +
            """Fields="No Type Status Gender Season Code Name City StartDate EndDate WebSite">""" +
            """<Filter Genders='W' Seasons='$season'/></Request>"""

    // 특정 대회의 경기 목록 (+ Pool/TeamA/TeamB relation).
    fun tournamentMatches(tournamentNo: Long): String =
        """<Request Type="GetVolleyMatchList" """ +
            """Fields="No Status DateTimeLocal DateTimeUtc CountryCode City Hall """ +
            """TeamAName TeamBName MatchPointsA MatchPointsB WebSite DurationTotal">""" +
            """<Filter NoTournament='$tournamentNo'/>""" +
            """<Relation Name='Pool' Fields='No Code Name'/>""" +
            """<Relation Name='TeamA' Fields='No Code Name'/>""" +
            """<Relation Name='TeamB' Fields='No Code Name'/></Request>"""

    // 특정 pool의 순위.
    fun poolRanking(poolNo: Long): String =
        """<Request Type="GetVolleyPoolRanking" NoPool="$poolNo" """ +
            """Fields="Position Rank TeamCode TeamName MatchesWon MatchesLost """ +
            """MatchPoints SetsWon SetsLost PointsWon PointsLost"/>"""
}
