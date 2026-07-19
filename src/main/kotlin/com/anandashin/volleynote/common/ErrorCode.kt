package com.anandashin.volleynote.common

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val status: HttpStatus,
) {
    // Common
    INTERNAL_SERVER_ERROR("INTERNAL_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("INVALID_INPUT", "Validation error", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_REQUIRED("AUTH_REQUIRED", "Authentication required", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN),

    // User
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    INVALID_PASSWORD("INVALID_PASSWORD", "Invalid password", HttpStatus.UNAUTHORIZED),
    LOGIN_USER_NOT_FOUND("LOGIN_USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
}
