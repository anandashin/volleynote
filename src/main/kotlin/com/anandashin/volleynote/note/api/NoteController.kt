package com.anandashin.volleynote.note.api

import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.NoteDTO
import com.anandashin.volleynote.note.dto.NoteSummaryDTO
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
import com.anandashin.volleynote.note.service.NoteService
import com.anandashin.volleynote.user.auth.AuthUser
import com.anandashin.volleynote.user.dto.UserDTO
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

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
) {
    @PostMapping
    fun create(
        @AuthUser user: UserDTO,
        @Valid @RequestBody request: CreateNoteRequest,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.createNote(user.id, request))
    }

    @GetMapping("/me")
    fun myNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getMyNotes(user.id, pageable))
    }

    @GetMapping("/bookmarks")
    fun bookmarkedNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getBookmarkedNotes(user.id, pageable))
    }

    @GetMapping
    fun publicNotes(
        @AuthUser user: UserDTO,
        pageable: Pageable,
    ): ResponseEntity<Page<NoteSummaryDTO>> {
        return ResponseEntity.ok(noteService.getPublicNotes(user.id, pageable))
    }

    @GetMapping("/{noteId}")
    fun getNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.getNote(noteId, user.id))
    }

    @PatchMapping("/{noteId}")
    fun updateNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
        @Valid @RequestBody request: UpdateNoteRequest,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.updateNote(noteId, user.id, request))
    }

    @DeleteMapping("/{noteId}")
    fun deleteNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<String> {
        noteService.deleteNote(noteId, user.id)
        return ResponseEntity.ok("Note deleted successfully")
    }

    @PostMapping("/{noteId}/like")
    fun likeNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.likeNote(noteId, user.id))
    }

    @DeleteMapping("/{noteId}/like")
    fun unlikeNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.unlikeNote(noteId, user.id))
    }

    @PostMapping("/{noteId}/bookmark")
    fun bookmarkNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.bookmarkNote(noteId, user.id))
    }

    @DeleteMapping("/{noteId}/bookmark")
    fun unbookmarkNote(
        @AuthUser user: UserDTO,
        @PathVariable noteId: Long,
    ): ResponseEntity<NoteDTO> {
        return ResponseEntity.ok(noteService.unbookmarkNote(noteId, user.id))
    }
}
