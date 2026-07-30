package com.anandashin.volleynote.note.service

import com.anandashin.volleynote.note.NoteAccessDeniedException
import com.anandashin.volleynote.note.NoteNotFoundException
import com.anandashin.volleynote.note.domain.NoteBookmarkEntity
import com.anandashin.volleynote.note.domain.NoteEntity
import com.anandashin.volleynote.note.domain.NoteLikeEntity
import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
import com.anandashin.volleynote.note.repository.NoteBookmarkRepository
import com.anandashin.volleynote.note.repository.NoteLikeRepository
import com.anandashin.volleynote.note.repository.NoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class NoteServiceImplTest {
    private val noteRepository: NoteRepository = mock()
    private val noteLikeRepository: NoteLikeRepository = mock()
    private val noteBookmarkRepository: NoteBookmarkRepository = mock()
    private val service: NoteService = NoteServiceImpl(noteRepository, noteLikeRepository, noteBookmarkRepository)

    @Test
    fun `createNote - authorId를 채워 저장하고 DTO 반환`() {
        whenever(noteRepository.save(any<NoteEntity>())).thenAnswer { it.arguments[0] }

        val dto =
            service.createNote(
                authorId = 1L,
                request = CreateNoteRequest(title = "직관 후기", content = "명경기였다"),
            )

        assertThat(dto.authorId).isEqualTo(1L)
        assertThat(dto.title).isEqualTo("직관 후기")
        assertThat(dto.content).isEqualTo("명경기였다")
        assertThat(dto.isPublic).isTrue()
        assertThat(dto.likeCount).isEqualTo(0)
        assertThat(dto.isLiked).isFalse()
        assertThat(dto.isBookmarked).isFalse()
        verify(noteRepository).save(any<NoteEntity>())
    }

    @Test
    fun `getNote - 공개 일지는 작성자가 아니어도 조회 가능`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = true)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        val dto = service.getNote(noteId = 5L, requesterId = 999L)

        assertThat(dto.id).isEqualTo(5L)
    }

    @Test
    fun `getNote - 비공개 일지는 작성자 본인은 조회 가능`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = false)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        val dto = service.getNote(noteId = 5L, requesterId = 1L)

        assertThat(dto.id).isEqualTo(5L)
    }

    @Test
    fun `getNote - 비공개 일지를 타인이 조회하면 NoteAccessDeniedException`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = false)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        assertThatThrownBy { service.getNote(noteId = 5L, requesterId = 2L) }
            .isInstanceOf(NoteAccessDeniedException::class.java)
    }

    @Test
    fun `getNote - 없거나 삭제된 일지면 NoteNotFoundException`() {
        whenever(noteRepository.findByIdAndDeletedAtIsNull(404L)).thenReturn(null)

        assertThatThrownBy { service.getNote(noteId = 404L, requesterId = 1L) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `getNote - 좋아요·북마크한 노트는 isLiked·isBookmarked true`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = true, likeCount = 3)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteLikeRepository.existsByUserIdAndNoteId(1L, 5L)).thenReturn(true)
        whenever(noteBookmarkRepository.existsByUserIdAndNoteId(1L, 5L)).thenReturn(true)

        val dto = service.getNote(noteId = 5L, requesterId = 1L)

        assertThat(dto.likeCount).isEqualTo(3)
        assertThat(dto.isLiked).isTrue()
        assertThat(dto.isBookmarked).isTrue()
    }

    @Test
    fun `updateNote - 제공된 필드만 변경 (title 있음, content 없음)`() {
        val note = noteEntity(id = 5L, authorId = 1L, title = "old", content = "keep-me")
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        val dto = service.updateNote(noteId = 5L, requesterId = 1L, request = UpdateNoteRequest(title = "new"))

        assertThat(dto.title).isEqualTo("new")
        assertThat(dto.content).isEqualTo("keep-me")
    }

    @Test
    fun `updateNote - 작성자가 아니면 NoteAccessDeniedException`() {
        val note = noteEntity(id = 5L, authorId = 1L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        assertThatThrownBy {
            service.updateNote(noteId = 5L, requesterId = 2L, request = UpdateNoteRequest(title = "hack"))
        }.isInstanceOf(NoteAccessDeniedException::class.java)
    }

    @Test
    fun `deleteNote - 작성자면 deletedAt을 채워 소프트 삭제 (hard delete 호출 안 함)`() {
        val note = noteEntity(id = 5L, authorId = 1L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        service.deleteNote(noteId = 5L, requesterId = 1L)

        assertThat(note.deletedAt).isNotNull()
        verify(noteRepository, never()).delete(any<NoteEntity>())
    }

    @Test
    fun `deleteNote - 작성자가 아니면 NoteAccessDeniedException`() {
        val note = noteEntity(id = 5L, authorId = 1L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        assertThatThrownBy { service.deleteNote(noteId = 5L, requesterId = 2L) }
            .isInstanceOf(NoteAccessDeniedException::class.java)
    }

    @Test
    fun `getPublicNotes - 엔티티를 NoteSummaryDTO로 매핑 + 좋아요 여부 배치 반영`() {
        val note = noteEntity(id = 3L, authorId = 7L, title = "공개 일지", likeCount = 10)
        whenever(noteRepository.findByIsPublicTrueAndDeletedAtIsNull(any()))
            .thenReturn(PageImpl(listOf(note)))
        whenever(noteLikeRepository.findLikedNoteIds(1L, listOf(3L))).thenReturn(listOf(3L))
        whenever(noteBookmarkRepository.findBookmarkedNoteIds(1L, listOf(3L))).thenReturn(emptyList())

        val page = service.getPublicNotes(requesterId = 1L, pageable = Pageable.unpaged())

        assertThat(page.content).hasSize(1)
        assertThat(page.content[0].id).isEqualTo(3L)
        assertThat(page.content[0].title).isEqualTo("공개 일지")
        assertThat(page.content[0].likeCount).isEqualTo(10)
        assertThat(page.content[0].isLiked).isTrue()
        assertThat(page.content[0].isBookmarked).isFalse()
    }

    @Test
    fun `likeNote - 최초 좋아요면 저장하고 likeCount 증가`() {
        val note = noteEntity(id = 5L, authorId = 1L, likeCount = 0)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        // 가드에서 false(아직 안 누름) → 상세 DTO 조회에서 true(방금 누름)
        whenever(noteLikeRepository.existsByUserIdAndNoteId(2L, 5L)).thenReturn(false, true)

        val dto = service.likeNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.likeCount).isEqualTo(1)
        assertThat(dto.isLiked).isTrue()
        verify(noteLikeRepository).save(any<NoteLikeEntity>())
    }

    @Test
    fun `likeNote - 이미 좋아요면 멱등 (저장·증가 없음)`() {
        val note = noteEntity(id = 5L, authorId = 1L, likeCount = 5)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteLikeRepository.existsByUserIdAndNoteId(2L, 5L)).thenReturn(true)

        val dto = service.likeNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.likeCount).isEqualTo(5)
        verify(noteLikeRepository, never()).save(any<NoteLikeEntity>())
    }

    @Test
    fun `unlikeNote - 좋아요 상태면 삭제하고 likeCount 감소`() {
        val note = noteEntity(id = 5L, authorId = 1L, likeCount = 3)
        val like = NoteLikeEntity(id = 9L, userId = 2L, noteId = 5L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteLikeRepository.findByUserIdAndNoteId(2L, 5L)).thenReturn(like)
        whenever(noteLikeRepository.existsByUserIdAndNoteId(2L, 5L)).thenReturn(false)

        val dto = service.unlikeNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.likeCount).isEqualTo(2)
        assertThat(dto.isLiked).isFalse()
        verify(noteLikeRepository).delete(like)
    }

    @Test
    fun `unlikeNote - 좋아요 안 한 상태면 멱등 (삭제·감소 없음)`() {
        val note = noteEntity(id = 5L, authorId = 1L, likeCount = 3)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteLikeRepository.findByUserIdAndNoteId(2L, 5L)).thenReturn(null)

        val dto = service.unlikeNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.likeCount).isEqualTo(3)
        verify(noteLikeRepository, never()).delete(any<NoteLikeEntity>())
    }

    @Test
    fun `bookmarkNote - 최초 북마크면 저장하고 isBookmarked true`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = true)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteBookmarkRepository.existsByUserIdAndNoteId(2L, 5L)).thenReturn(false, true)

        val dto = service.bookmarkNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.isBookmarked).isTrue()
        verify(noteBookmarkRepository).save(any<NoteBookmarkEntity>())
    }

    @Test
    fun `unbookmarkNote - 북마크 상태면 삭제하고 isBookmarked false`() {
        val note = noteEntity(id = 5L, authorId = 1L)
        val bookmark = NoteBookmarkEntity(id = 8L, userId = 2L, noteId = 5L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)
        whenever(noteBookmarkRepository.findByUserIdAndNoteId(2L, 5L)).thenReturn(bookmark)
        whenever(noteBookmarkRepository.existsByUserIdAndNoteId(2L, 5L)).thenReturn(false)

        val dto = service.unbookmarkNote(noteId = 5L, requesterId = 2L)

        assertThat(dto.isBookmarked).isFalse()
        verify(noteBookmarkRepository).delete(bookmark)
    }

    @Test
    fun `getBookmarkedNotes - 북마크한 활성 노트를 요약으로 반환 (isBookmarked true)`() {
        val note = noteEntity(id = 3L, authorId = 7L, title = "북마크한 일지")
        whenever(noteRepository.findBookmarkedNotes(1L, Pageable.unpaged()))
            .thenReturn(PageImpl(listOf(note)))
        whenever(noteLikeRepository.findLikedNoteIds(1L, listOf(3L))).thenReturn(emptyList())
        whenever(noteBookmarkRepository.findBookmarkedNoteIds(1L, listOf(3L))).thenReturn(listOf(3L))

        val page = service.getBookmarkedNotes(requesterId = 1L, pageable = Pageable.unpaged())

        assertThat(page.content).hasSize(1)
        assertThat(page.content[0].id).isEqualTo(3L)
        assertThat(page.content[0].isBookmarked).isTrue()
    }

    @Test
    fun `likeNote - 비공개 타인 노트면 NoteAccessDeniedException`() {
        val note = noteEntity(id = 5L, authorId = 1L, isPublic = false)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(note)

        assertThatThrownBy { service.likeNote(noteId = 5L, requesterId = 2L) }
            .isInstanceOf(NoteAccessDeniedException::class.java)
        verify(noteLikeRepository, never()).save(any<NoteLikeEntity>())
    }

    private fun noteEntity(
        id: Long = 1L,
        authorId: Long = 1L,
        title: String = "title",
        content: String = "content",
        isPublic: Boolean = true,
        likeCount: Int = 0,
    ) = NoteEntity(
        id = id,
        authorId = authorId,
        title = title,
        content = content,
        isPublic = isPublic,
        likeCount = likeCount,
    )
}
