package com.anandashin.volleynote.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false, name = "email")
    var email: String = "",

    @Column(nullable = false, name = "nickname")
    var nickname: String = "",

    @Column(nullable = false, name = "hashed_password")
    var hashedPassword: String = "",

    @Column(name = "introduction")
    var introduction: String? = null,
)
