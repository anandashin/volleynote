package com.anandashin.volleynote.fivb.sync

// FIVB 동기화 결과 요약(적재 건수).
data class FivbSyncResult(
    val season: Int,
    val tournaments: Int,
    val matches: Int,
    val rankings: Int,
)
