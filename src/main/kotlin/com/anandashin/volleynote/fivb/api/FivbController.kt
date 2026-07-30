package com.anandashin.volleynote.fivb.api

import com.anandashin.volleynote.fivb.dto.FivbMatchDTO
import com.anandashin.volleynote.fivb.dto.FivbRankingDTO
import com.anandashin.volleynote.fivb.dto.FivbTournamentDTO
import com.anandashin.volleynote.fivb.service.FivbService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// FIVB 미러 읽기 전용 조회 API. 여자 국가대표 대회 → 경기 → 순위.
@RestController
@RequestMapping("/api/fivb")
class FivbController(
    private val fivbService: FivbService,
) {
    @GetMapping("/tournaments")
    fun tournaments(
        @RequestParam(required = false) season: Int?,
    ): ResponseEntity<List<FivbTournamentDTO>> {
        return ResponseEntity.ok(fivbService.getTournaments(season))
    }

    @GetMapping("/tournaments/{fivbNo}")
    fun tournament(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<FivbTournamentDTO> {
        return ResponseEntity.ok(fivbService.getTournament(fivbNo))
    }

    @GetMapping("/tournaments/{fivbNo}/matches")
    fun tournamentMatches(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<List<FivbMatchDTO>> {
        return ResponseEntity.ok(fivbService.getMatches(fivbNo))
    }

    @GetMapping("/tournaments/{fivbNo}/rankings")
    fun tournamentRankings(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<List<FivbRankingDTO>> {
        return ResponseEntity.ok(fivbService.getRankings(fivbNo))
    }

    @GetMapping("/matches/{fivbNo}")
    fun match(
        @PathVariable fivbNo: Long,
    ): ResponseEntity<FivbMatchDTO> {
        return ResponseEntity.ok(fivbService.getMatch(fivbNo))
    }
}
