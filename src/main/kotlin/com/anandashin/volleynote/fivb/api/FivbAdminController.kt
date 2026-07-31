package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.sync.FivbSyncResult
import com.anandashin.volleynote.fivb.sync.FivbSyncService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year

// FIVB 수동 동기화 트리거 (ADMIN 전용 — /api/admin/** 인가 규칙 적용).
@Tag(name = "FIVB Admin", description = "FIVB 동기화 (ADMIN 전용)")
@RestController
@RequestMapping("/api/admin/fivb")
class FivbAdminController(
    private val fivbSyncService: FivbSyncService,
) {
    @Operation(
        summary = "FIVB 수동 동기화",
        description = "FIVB VIS에서 대회·경기·순위를 적재. ADMIN 권한 필요. season 미지정 시 올해.",
    )
    @PostMapping("/sync")
    fun sync(
        @RequestParam(required = false) season: Int?,
    ): ResponseEntity<FivbSyncResult> {
        val target = season ?: Year.now().value
        return ResponseEntity.ok(fivbSyncService.sync(target))
    }
}
