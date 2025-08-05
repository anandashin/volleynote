package com.anandashin.volleynote.user.api

import com.anandashin.volleynote.user.dto.SignUpRequest
import com.anandashin.volleynote.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignUpRequest
    ) : ResponseEntity<String> {
        val result = userService.createUser(
            request.email, request.password, request.nickname, request.introduction
        )
        return ResponseEntity.ok(result)
    }
}
