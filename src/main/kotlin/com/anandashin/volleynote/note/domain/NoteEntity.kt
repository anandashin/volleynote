package com.anandashin.volleynote.note.domain

import com.anandashin.volleynote.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "notes")
class NoteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, name = "author_id", updatable = false)
    val authorId: Long = 0,
    @Column(nullable = false, name = "title", length = 100)
    var title: String = "",
    @Column(nullable = false, name = "content", columnDefinition = "TEXT")
    var content: String = "",
    @Column(name = "match_date")
    var matchDate: LocalDate? = null,
    @Column(name = "home_team", length = 50)
    var homeTeam: String? = null,
    @Column(name = "away_team", length = 50)
    var awayTeam: String? = null,
    @Column(nullable = false, name = "is_public")
    var isPublic: Boolean = true,
) : BaseEntity()
