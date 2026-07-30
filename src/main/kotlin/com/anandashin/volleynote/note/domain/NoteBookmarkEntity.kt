package com.anandashin.volleynote.note.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "note_bookmarks",
    uniqueConstraints = [UniqueConstraint(name = "uk_note_bookmark", columnNames = ["user_id", "note_id"])],
)
class NoteBookmarkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: Long = 0,
    @Column(name = "note_id", nullable = false, updatable = false)
    val noteId: Long = 0,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
