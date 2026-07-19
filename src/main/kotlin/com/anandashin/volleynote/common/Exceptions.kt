package com.anandashin.volleynote.common

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

open class Exceptions(
    val httpErrorCode: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    val msg: String,
    cause: Throwable? = null,
) : RuntimeException(msg, cause) {
    override fun toString(): String = "Exceptions(msg='$msg', httpErrorCode=$httpErrorCode)"
}
