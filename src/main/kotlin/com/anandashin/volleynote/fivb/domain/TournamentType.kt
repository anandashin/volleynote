package com.anandashin.volleynote.fivb.domain

// VIS SDK VolleyTournamentType (docs/fivb-mirror-schema.md §3.2)
enum class TournamentType(val code: Int) {
    UNKNOWN(1),
    OLYMPIC_GAMES(2),
    WORLD_CHAMPIONSHIP(3),
    WORLD_GRAND_PRIX(4),
    WORLD_LEAGUE(5),
    TEST(6),
    CEV_CHALLENGE_CUP(7),
    CEV_CHAMPIONS_LEAGUE(8),
    CEV_CUP(9),
    CONTINENTAL_CHAMPIONSHIP(10),
    OLYMPIC_GAMES_QUALIFICATION(11),
    NATIONS_LEAGUE(12),
    CHALLENGER_CUP(13),
    CLUB_WORLD_CHAMPIONSHIP(14),
    WORLD_CUP(15),
    AGE_GROUP_WORLD_CHAMPIONSHIP(16),
    OTHER(17),
    NATIONAL_LEAGUE(18),
    CONTINENTAL_LEAGUE(19),
    ZONAL_CHAMPIONSHIP(20),
    WORLD_CHAMPIONSHIP_QUALIFICATION(21),
    ;

    companion object {
        // 국가대표 senior 화이트리스트 (schema §4 핵심)
        val NATIONAL_TEAM_SENIOR_CODES: Set<Int> =
            setOf(
                OLYMPIC_GAMES.code,
                WORLD_CHAMPIONSHIP.code,
                WORLD_CUP.code,
                NATIONS_LEAGUE.code,
                CONTINENTAL_CHAMPIONSHIP.code,
            )

        fun fromCode(code: Int?): TournamentType = entries.find { it.code == code } ?: UNKNOWN
    }
}
