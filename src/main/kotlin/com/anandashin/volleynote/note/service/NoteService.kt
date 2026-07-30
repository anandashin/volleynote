package com.anandashin.volleynote.note.service

import com.anandashin.volleynote.note.NoteAccessDeniedException
import com.anandashin.volleynote.note.NoteNotFoundException
import com.anandashin.volleynote.note.domain.NoteEntity
import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.NoteDTO
import com.anandashin.volleynote.note.dto.NoteSummaryDTO
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
import com.anandashin.volleynote.note.repository.NoteRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface NoteService {
    fun createNote(
        authorId: Long,
        request: CreateNoteRequest,
    ): NoteDTO

    fun getNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO

    fun getMyNotes(
        authorId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO>

    fun getPublicNotes(pageable: Pageable): Page<NoteSummaryDTO>

    fun updateNote(
        noteId: Long,
        requesterId: Long,
        request: UpdateNoteRequest,
    ): NoteDTO

    fun deleteNote(
        noteId: Long,
        requesterId: Long,
    )
}

@Service
open class NoteServiceImpl(
    private val noteRepository: NoteRepository,
) : NoteService {
    @Transactional
    override fun createNote(
        authorId: Long,
        request: CreateNoteRequest,
    ): NoteDTO {
        val note =
            noteRepository.save(
                NoteEntity(
                    authorId = authorId,
                    title = request.title,
                    content = request.content,
                    matchDate = request.matchDate,
                    homeTeam = request.homeTeam,
                    awayTeam = request.awayTeam,
                    isPublic = request.isPublic,
                ),
            )
        return NoteDTO.from(note)
    }

    override fun getNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        if (!note.isPublic && note.authorId != requesterId) {
            throw NoteAccessDeniedException()
        }
        return NoteDTO.from(note)
    }

    override fun getMyNotes(
        authorId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO> = noteRepository.findByAuthorIdAndDeletedAtIsNull(authorId, pageable).map(NoteSummaryDTO::from)

    override fun getPublicNotes(pageable: Pageable): Page<NoteSummaryDTO> =
        noteRepository.findByIsPublicTrueAndDeletedAtIsNull(pageable).map(NoteSummaryDTO::from)

    @Transactional
    override fun updateNote(
        noteId: Long,
        requesterId: Long,
        request: UpdateNoteRequest,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        if (note.authorId != requesterId) {
            throw NoteAccessDeniedException()
        }
        request.title?.let { note.title = it }
        request.content?.let { note.content = it }
        request.matchDate?.let { note.matchDate = it }
        request.homeTeam?.let { note.homeTeam = it }
        request.awayTeam?.let { note.awayTeam = it }
        request.isPublic?.let { note.isPublic = it }
        return NoteDTO.from(note)
    }

    @Transactional
    override fun deleteNote(
        noteId: Long,
        requesterId: Long,
    ) {
        val note = findActiveNote(noteId)
        if (note.authorId != requesterId) {
            throw NoteAccessDeniedException()
        }
        note.deletedAt = LocalDateTime.now()
    }

    private fun findActiveNote(noteId: Long): NoteEntity =
        noteRepository.findByIdAndDeletedAtIsNull(noteId) ?: throw NoteNotFoundException()
}
