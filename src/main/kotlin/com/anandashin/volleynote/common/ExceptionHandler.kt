package com.anandashin.volleynote.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        val ec = e.errorCode
        return ResponseEntity.status(ec.status).body(ErrorResponse.of(ec))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val fieldErrors =
            ex.bindingResult.fieldErrors.map {
                ErrorResponse.FieldError(field = it.field, message = it.defaultMessage)
            }
        val ec = ErrorCode.INVALID_INPUT
        return ResponseEntity.status(ec.status).body(ErrorResponse.of(ec, fieldErrors))
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val ec = ErrorCode.INVALID_INPUT
        return ResponseEntity.status(ec.status).body(ErrorResponse.of(ec))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", e)
        val ec = ErrorCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(ec.status).body(ErrorResponse.of(ec))
    }
}
