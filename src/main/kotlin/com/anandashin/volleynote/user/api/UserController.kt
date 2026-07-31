package com.anandashin.volleynote.user.api

import com.anandashin.volleynote.user.auth.AuthUser
import com.anandashin.volleynote.user.dto.PublicUserDTO
import com.anandashin.volleynote.user.dto.SignInRequest
import com.anandashin.volleynote.user.dto.SignInResponse
import com.anandashin.volleynote.user.dto.SignUpRequest
import com.anandashin.volleynote.user.dto.UpdateMeRequest
import com.anandashin.volleynote.user.dto.UserDTO
import com.anandashin.volleynote.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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

@Tag(name = "User", description = "회원가입·로그인·프로필")
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "회원가입", description = "이메일/비밀번호로 가입. 인증 불필요.")
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

    @Operation(summary = "로그인", description = "성공 시 JWT accessToken 발급. 인증 불필요.")
    @PostMapping("/login")
    fun login(
        @RequestBody request: SignInRequest,
    ): ResponseEntity<SignInResponse> {
        val (user, accessToken) = userService.login(request.email, request.password)
        return ResponseEntity.ok(SignInResponse(user.id, accessToken))
    }

    @Operation(summary = "내 정보 조회", description = "토큰의 현재 사용자 정보.")
    @GetMapping("/me")
    fun me(
        @AuthUser user: UserDTO,
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(user)
    }

    @Operation(summary = "내 정보 수정", description = "nickname·introduction 부분 수정.")
    @PatchMapping("/me")
    fun updateMe(
        @AuthUser user: UserDTO,
        @Valid @RequestBody request: UpdateMeRequest,
    ): ResponseEntity<String> {
        userService.updateMe(user.id, request)
        return ResponseEntity.ok("success")
    }

    @Operation(summary = "회원 탈퇴", description = "현재 사용자 삭제(하드 삭제).")
    @DeleteMapping("/me")
    fun deleteMe(
        @AuthUser user: UserDTO,
    ): ResponseEntity<String> {
        userService.deleteMe(user.id)
        return ResponseEntity.ok("Account deleted successfully")
    }

    @Operation(summary = "공개 프로필 조회", description = "다른 사용자의 공개 정보(email 제외).")
    @GetMapping("/{userId}")
    fun getPublicProfile(
        @PathVariable userId: Long,
    ): ResponseEntity<PublicUserDTO> {
        return ResponseEntity.ok(userService.getPublicProfile(userId))
    }
}
