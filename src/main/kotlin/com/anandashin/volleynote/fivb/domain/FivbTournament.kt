package com.anandashin.volleynote.fivb.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

// FIVB 미러(B안: Spring 소유·적재·읽기). schema §2.1
@Entity
@Table(name = "fivb_tournament")
class FivbTournament(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "fivb_no")
    val fivbNo: Long = 0,
    @Column(name = "code")
    val code: String? = null,
    @Column(name = "name")
    val name: String = "",
    @Column(name = "type_code")
    val typeCode: Int? = null,
    @Column(name = "status_code")
    val statusCode: Int? = null,
    @Column(name = "gender")
    val gender: String? = null,
    @Column(name = "season")
    val season: Int? = null,
    @Column(name = "city")
    val city: String? = null,
    @Column(name = "start_date")
    val startDate: LocalDate? = null,
    @Column(name = "end_date")
    val endDate: LocalDate? = null,
    @Column(name = "web_site")
    val webSite: String? = null,
    @Column(name = "synced_at")
    val syncedAt: LocalDateTime? = null,
)
