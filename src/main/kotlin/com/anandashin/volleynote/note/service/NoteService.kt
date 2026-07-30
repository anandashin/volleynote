package com.anandashin.volleynote.note.service

import com.anandashin.volleynote.note.NoteAccessDeniedException
import com.anandashin.volleynote.note.NoteNotFoundException
import com.anandashin.volleynote.note.domain.NoteBookmarkEntity
import com.anandashin.volleynote.note.domain.NoteEntity
import com.anandashin.volleynote.note.domain.NoteLikeEntity
import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.NoteDTO
import com.anandashin.volleynote.note.dto.NoteSummaryDTO
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
import com.anandashin.volleynote.note.repository.NoteBookmarkRepository
import com.anandashin.volleynote.note.repository.NoteLikeRepository
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
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO>

    fun getPublicNotes(
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO>

    fun getBookmarkedNotes(
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO>

    fun updateNote(
        noteId: Long,
        requesterId: Long,
        request: UpdateNoteRequest,
    ): NoteDTO

    fun deleteNote(
        noteId: Long,
        requesterId: Long,
    )

    fun likeNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO

    fun unlikeNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO

    fun bookmarkNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO

    fun unbookmarkNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO
}

@Service
open class NoteServiceImpl(
    private val noteRepository: NoteRepository,
    private val noteLikeRepository: NoteLikeRepository,
    private val noteBookmarkRepository: NoteBookmarkRepository,
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
        // 방금 생성 → 아직 좋아요/북마크 없음
        return NoteDTO.from(note, isLiked = false, isBookmarked = false)
    }

    override fun getNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        assertReadable(note, requesterId)
        return toDetailDto(note, requesterId)
    }

    override fun getMyNotes(
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO> = toSummaryPage(noteRepository.findByAuthorIdAndDeletedAtIsNull(requesterId, pageable), requesterId)

    override fun getPublicNotes(
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO> = toSummaryPage(noteRepository.findByIsPublicTrueAndDeletedAtIsNull(pageable), requesterId)

    override fun getBookmarkedNotes(
        requesterId: Long,
        pageable: Pageable,
    ): Page<NoteSummaryDTO> = toSummaryPage(noteRepository.findBookmarkedNotes(requesterId, pageable), requesterId)

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
        return toDetailDto(note, requesterId)
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

    @Transactional
    override fun likeNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        assertReadable(note, requesterId)
        // 멱등: 이미 눌렀으면 카운트 증가 없음
        if (!noteLikeRepository.existsByUserIdAndNoteId(requesterId, noteId)) {
            noteLikeRepository.save(NoteLikeEntity(userId = requesterId, noteId = noteId))
            note.likeCount += 1
        }
        return toDetailDto(note, requesterId)
    }

    @Transactional
    override fun unlikeNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        val like = noteLikeRepository.findByUserIdAndNoteId(requesterId, noteId)
        if (like != null) {
            noteLikeRepository.delete(like)
            if (note.likeCount > 0) note.likeCount -= 1
        }
        return toDetailDto(note, requesterId)
    }

    @Transactional
    override fun bookmarkNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        assertReadable(note, requesterId)
        if (!noteBookmarkRepository.existsByUserIdAndNoteId(requesterId, noteId)) {
            noteBookmarkRepository.save(NoteBookmarkEntity(userId = requesterId, noteId = noteId))
        }
        return toDetailDto(note, requesterId)
    }

    @Transactional
    override fun unbookmarkNote(
        noteId: Long,
        requesterId: Long,
    ): NoteDTO {
        val note = findActiveNote(noteId)
        noteBookmarkRepository.findByUserIdAndNoteId(requesterId, noteId)?.let {
            noteBookmarkRepository.delete(it)
        }
        return toDetailDto(note, requesterId)
    }

    private fun findActiveNote(noteId: Long): NoteEntity =
        noteRepository.findByIdAndDeletedAtIsNull(noteId) ?: throw NoteNotFoundException()

    // 조회 가능 여부: 공개이거나 작성자 본인
    private fun assertReadable(
        note: NoteEntity,
        requesterId: Long,
    ) {
        if (!note.isPublic && note.authorId != requesterId) {
            throw NoteAccessDeniedException()
        }
    }

    private fun toDetailDto(
        note: NoteEntity,
        requesterId: Long,
    ): NoteDTO =
        NoteDTO.from(
            entity = note,
            isLiked = noteLikeRepository.existsByUserIdAndNoteId(requesterId, note.id),
            isBookmarked = noteBookmarkRepository.existsByUserIdAndNoteId(requesterId, note.id),
        )

    // 목록: 좋아요/북마크 여부를 한 번의 IN 쿼리로 배치 조회 (N+1 방지)
    private fun toSummaryPage(
        page: Page<NoteEntity>,
        requesterId: Long,
    ): Page<NoteSummaryDTO> {
        val ids = page.content.map { it.id }
        if (ids.isEmpty()) return page.map { NoteSummaryDTO.from(it, isLiked = false, isBookmarked = false) }
        val likedIds = noteLikeRepository.findLikedNoteIds(requesterId, ids).toSet()
        val bookmarkedIds = noteBookmarkRepository.findBookmarkedNoteIds(requesterId, ids).toSet()
        return page.map {
            NoteSummaryDTO.from(
                entity = it,
                isLiked = it.id in likedIds,
                isBookmarked = it.id in bookmarkedIds,
            )
        }
    }
}
