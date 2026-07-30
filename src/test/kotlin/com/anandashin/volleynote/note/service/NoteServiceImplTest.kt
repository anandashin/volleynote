package com.anandashin.volleynote.note.service

import com.anandashin.volleynote.note.NoteAccessDeniedException
import com.anandashin.volleynote.note.NoteNotFoundException
import com.anandashin.volleynote.note.domain.NoteEntity
import com.anandashin.volleynote.note.dto.CreateNoteRequest
import com.anandashin.volleynote.note.dto.UpdateNoteRequest
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
    private val service: NoteService = NoteServiceImpl(noteRepository)

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
    fun `getPublicNotes - 엔티티를 NoteSummaryDTO로 매핑해 반환`() {
        val note = noteEntity(id = 3L, authorId = 7L, title = "공개 일지")
        whenever(noteRepository.findByIsPublicTrueAndDeletedAtIsNull(any()))
            .thenReturn(PageImpl(listOf(note)))

        val page = service.getPublicNotes(Pageable.unpaged())

        assertThat(page.content).hasSize(1)
        assertThat(page.content[0].id).isEqualTo(3L)
        assertThat(page.content[0].title).isEqualTo("공개 일지")
    }

    private fun noteEntity(
        id: Long = 1L,
        authorId: Long = 1L,
        title: String = "title",
        content: String = "content",
        isPublic: Boolean = true,
    ) = NoteEntity(
        id = id,
        authorId = authorId,
        title = title,
        content = content,
        isPublic = isPublic,
    )
}
