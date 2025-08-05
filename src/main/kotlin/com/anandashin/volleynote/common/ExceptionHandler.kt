package com.anandashin.volleynote.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    data class ErrorResponse(
        val message: String,
        val status: Int,
        val errors: List<FieldError>? = null
    )

    data class FieldError(
        val field: String,
        val message: String?
    )

    @ExceptionHandler(Exceptions::class)
    fun handleCustomExceptions(e: Exceptions): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = e.msg,
            status = e.httpErrorCode.value(),
        )
        return ResponseEntity(errorResponse, e.httpErrorCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldError = e.bindingResult?.fieldErrors?.map {
            FieldError(field = it.field, message = it.defaultMessage)
        }
        val errorResponse = ErrorResponse(
            message = "Validation error",
            status = HttpStatus.BAD_REQUEST.value(),
            errors = fieldError
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
