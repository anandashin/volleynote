package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.sync.FivbSyncResult
import com.anandashin.volleynote.fivb.sync.FivbSyncService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year

// FIVB 수동 동기화 트리거 (ADMIN 전용 — /api/admin/** 인가 규칙 적용).
@RestController
@RequestMapping("/api/admin/fivb")
class FivbAdminController(
    private val fivbSyncService: FivbSyncService,
) {
    @PostMapping("/sync")
    fun sync(
        @RequestParam(required = false) season: Int?,
    ): ResponseEntity<FivbSyncResult> {
        val target = season ?: Year.now().value
        return ResponseEntity.ok(fivbSyncService.sync(target))
    }
}
