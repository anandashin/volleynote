package com.anandashin.volleynote.note.api

import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.NoteDTO
import com.anandashin.volleynote.note.dto.NoteSummaryDTO
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
import com.anandashin.volleynote.note.service.NoteService
import com.anandashin.volleynote.user.auth.AuthUser
import com.anandashin.volleynote.user.dto.UserDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Note", description = "관전 일지 + 좋아요·북마크")
@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
) {
    @Operation(summary = "일지 작성")
    @PostMapping
    fun create(
        @AuthUser user: UserDTO,
        @Valid @RequestBody request: CreateNoteRequest,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.createNote(user.id, request))
    }

    @Operation(summary = "내 일지 목록", description = "페이징.")
    @GetMapping("/me")
    fun myNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getMyNotes(user.id, pageable))
    }

    @Operation(summary = "내 북마크 목록", description = "페이징.")
    @GetMapping("/bookmarks")
    fun bookmarkedNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getBookmarkedNotes(user.id, pageable))
    }

    @Operation(summary = "공개 일지 피드", description = "공개 일지 목록(페이징).")
    @GetMapping
    fun publicNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getPublicNotes(user.id, pageable))
    }

    @Operation(summary = "일지 상세", description = "비공개 일지는 작성자만.")
    @GetMapping("/{noteId}")
    fun getNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.getNote(noteId, user.id))
    }

    @Operation(summary = "일지 수정", description = "작성자만. 부분 수정.")
    @PatchMapping("/{noteId}")
    fun updateNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
        @Valid @RequestBody request: UpdateNoteRequest,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.updateNote(noteId, user.id, request))
    }

    @Operation(summary = "일지 삭제", description = "작성자만. 소프트 삭제.")
    @DeleteMapping("/{noteId}")
    fun deleteNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<String> {
        noteService.deleteNote(noteId, user.id)
        return ResponseEntity.ok("Note deleted successfully")
    }

    @Operation(summary = "좋아요", description = "멱등.")
    @PostMapping("/{noteId}/like")
    fun likeNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.likeNote(noteId, user.id))
    }

    @Operation(summary = "좋아요 취소", description = "멱등.")
    @DeleteMapping("/{noteId}/like")
    fun unlikeNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.unlikeNote(noteId, user.id))
    }

    @Operation(summary = "북마크", description = "멱등.")
    @PostMapping("/{noteId}/bookmark")
    fun bookmarkNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.bookmarkNote(noteId, user.id))
    }

    @Operation(summary = "북마크 취소", description = "멱등.")
    @DeleteMapping("/{noteId}/bookmark")
    fun unbookmarkNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.unbookmarkNote(noteId, user.id))
    }
}
