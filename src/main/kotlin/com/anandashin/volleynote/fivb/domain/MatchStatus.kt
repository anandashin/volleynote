package com.anandashin.volleynote.fivb.domain

// VIS SDK VolleyMatchStatus를 요약 매핑 (docs/fivb-mirror-schema.md §3.2)
// 1~3 예정, 4~23 세트별 진행, 24~27 종료(24 Finished / 25 OfficialResult / 26 Corrected / 27 Closed)
enum class MatchStatus {
    SCHEDULED,
    LIVE,
    FINISHED,
    UNKNOWN,
    ;

    companion object {
        fun fromCode(code: Int?): MatchStatus =
            when (code) {
                in 1..3 -> SCHEDULED
                in 4..23 -> LIVE
                in 24..27 -> FINISHED
                else -> UNKNOWN
            }
    }
}
