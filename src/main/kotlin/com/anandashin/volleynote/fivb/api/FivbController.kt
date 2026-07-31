package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.dto.FivbMatchDTO
import com.anandashin.volleynote.fivb.dto.FivbRankingDTO
import com.anandashin.volleynote.fivb.dto.FivbTournamentDTO
import com.anandashin.volleynote.fivb.service.FivbService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// FIVB 미러 읽기 전용 조회 API. 여자 국가대표 대회 → 경기 → 순위.
@Tag(name = "FIVB", description = "FIVB 미러 조회 (여자 국가대표 대회·경기·순위)")
@RestController
@RequestMapping("/api/fivb")
class FivbController(
    private val fivbService: FivbService,
) {
    @Operation(summary = "대회 목록", description = "국가대표 대회. season 필터 선택.")
    @GetMapping("/tournaments")
    fun tournaments(
        @RequestParam(required = false) season: Int?,
    ): ResponseEntity<List<FivbTournamentDTO>> {
        return ResponseEntity.ok(fivbService.getTournaments(season))
    }

    @Operation(summary = "대회 상세")
    @GetMapping("/tournaments/{fivbNo}")
    fun tournament(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<FivbTournamentDTO> {
        return ResponseEntity.ok(fivbService.getTournament(fivbNo))
    }

    @Operation(summary = "대회별 경기 목록", description = "경기 시각 순.")
    @GetMapping("/tournaments/{fivbNo}/matches")
    fun tournamentMatches(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<List<FivbMatchDTO>> {
        return ResponseEntity.ok(fivbService.getMatches(fivbNo))
    }

    @Operation(summary = "대회별 순위", description = "풀별 순위.")
    @GetMapping("/tournaments/{fivbNo}/rankings")
    fun tournamentRankings(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<List<FivbRankingDTO>> {
        return ResponseEntity.ok(fivbService.getRankings(fivbNo))
    }

    @Operation(summary = "경기 상세")
    @GetMapping("/matches/{fivbNo}")
    fun match(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<FivbMatchDTO> {
        return ResponseEntity.ok(fivbService.getMatch(fivbNo))
    }
}
