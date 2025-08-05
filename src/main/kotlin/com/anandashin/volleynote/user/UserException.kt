package com.anandashin.volleynote.user

import com.anandashin.volleynote.common.Exceptions
import org.springframework.http.HttpStatusCode

sealed class UserException(
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : Exceptions(httpStatusCode, msg, cause) {
}


