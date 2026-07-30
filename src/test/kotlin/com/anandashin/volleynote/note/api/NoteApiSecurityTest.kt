package com.anandashin.volleynote.note.api

import com.anandashin.volleynote.note.domain.NoteEntity
import com.anandashin.volleynote.note.repository.NoteRepository
import com.anandashin.volleynote.user.auth.JwtTokenProvider
import com.anandashin.volleynote.user.domain.Role
import com.anandashin.volleynote.user.domain.UserEntity
import com.anandashin.volleynote.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteApiSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var noteRepository: NoteRepository

    // --- POST /api/notes ---

    @Test
    fun `일지 작성은 토큰 없으면 401`() {
        mockMvc
            .perform(
                post("/api/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title":"t","content":"c"}"""),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `일지 작성은 유효 토큰과 정상 body면 200`() {
        authenticate(1L)
        whenever(noteRepository.save(any<NoteEntity>())).thenAnswer { it.arguments[0] }
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(
                post("/api/notes")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title":"직관 후기","content":"명경기","isPublic":true}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("직관 후기"))
            .andExpect(jsonPath("$.authorId").value(1))
    }

    @Test
    fun `일지 작성은 title이 비면 400 + INVALID_INPUT`() {
        authenticate(1L)
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(
                post("/api/notes")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title":"","content":"c"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
            .andExpect(jsonPath("$.errors").isArray)
    }

    // --- GET /api/notes/{id} ---

    @Test
    fun `비공개 일지를 타인이 조회하면 403 + NOTE_ACCESS_DENIED`() {
        authenticate(2L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L))
            .thenReturn(noteEntity(id = 5L, authorId = 1L, isPublic = false))
        val token = JwtTokenProvider.createJwtToken(2L)

        mockMvc
            .perform(get("/api/notes/5").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.code").value("NOTE_ACCESS_DENIED"))
            .andExpect(jsonPath("$.status").value(403))
    }

    @Test
    fun `없는 일지를 조회하면 404 + NOTE_NOT_FOUND`() {
        authenticate(1L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(null)
        val token = JwtTokenProvider.createJwtToken(1L)

        mockMvc
            .perform(get("/api/notes/999").header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("NOTE_NOT_FOUND"))
    }

    // --- DELETE /api/notes/{id} ---

    @Test
    fun `타인의 일지를 삭제하면 403 + NOTE_ACCESS_DENIED`() {
        authenticate(2L)
        whenever(noteRepository.findByIdAndDeletedAtIsNull(5L))
            .thenReturn(noteEntity(id = 5L, authorId = 1L, isPublic = true))
        val token = JwtTokenProvider.createJwtToken(2L)

        mockMvc
            .perform(delete("/api/notes/5").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.code").value("NOTE_ACCESS_DENIED"))
    }

    // 인증 필터가 조회하는 사용자 stub (JwtAuthenticationFilter -> userRepository.findById)
    private fun authenticate(userId: Long) {
        whenever(userRepository.findById(userId)).thenReturn(
            Optional.of(
                UserEntity(
                    id = userId,
                    email = "u$userId@a.com",
                    nickname = "u$userId",
                    hashedPassword = "irrelevant",
                    role = Role.USER,
                ),
            ),
        )
    }

    private fun noteEntity(
        id: Long,
        authorId: Long,
        isPublic: Boolean,
    ) = NoteEntity(
        id = id,
        authorId = authorId,
        title = "title",
        content = "content",
        isPublic = isPublic,
    )
}
