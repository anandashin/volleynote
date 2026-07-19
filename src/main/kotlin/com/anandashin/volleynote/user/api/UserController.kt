package com.anandashin.volleynote.user.api

import com.anandashin.volleynote.user.auth.AuthUser
import com.anandashin.volleynote.user.dto.PublicUserDTO
import com.anandashin.volleynote.user.dto.SignInRequest
import com.anandashin.volleynote.user.dto.SignInResponse
import com.anandashin.volleynote.user.dto.SignUpRequest
import com.anandashin.volleynote.user.dto.UpdateMeRequest
import com.anandashin.volleynote.user.dto.UserDTO
import com.anandashin.volleynote.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignUpRequest,
    ): ResponseEntity<String> {
        val result =
            userService.createUser(
                request.email,
                request.password,
                request.nickname,
                request.introduction,
            )
        return ResponseEntity.ok(result)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: SignInRequest,
    ): ResponseEntity<SignInResponse> {
        val (user, accessToken) = userService.login(request.email, request.password)
        return ResponseEntity.ok(SignInResponse(user.id, accessToken))
    }

    @GetMapping("/me")
    fun me(
        @AuthUser user: UserDTO,
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(user)
    }

    @PatchMapping("/me")
    fun updateMe(
        @AuthUser user: UserDTO,
        @Valid @RequestBody request: UpdateMeRequest,
    ): ResponseEntity<String> {
        userService.updateMe(user.id, request)
        return ResponseEntity.ok("success")
    }

    @DeleteMapping("/me")
    fun deleteMe(
        @AuthUser user: UserDTO,
    ): ResponseEntity<String> {
        userService.deleteMe(user.id)
        return ResponseEntity.ok("Account deleted successfully")
    }

    @GetMapping("/{userId}")
    fun getPublicProfile(
        @PathVariable userId: Long,
    ): ResponseEntity<PublicUserDTO> {
        return ResponseEntity.ok(userService.getPublicProfile(userId))
    }
}
