package com.anandashin.volleynote.user

import com.anandashin.volleynote.common.Exceptions
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class UserException(
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : Exceptions(httpStatusCode, msg, cause)

class SignUpEmailConflictException :
    UserException(
        httpStatusCode = HttpStatus.CONFLICT,
        msg = "Email already exists",
    )

class LoginInvalidPasswordException :
    UserException(
        httpStatusCode = HttpStatus.UNAUTHORIZED,
        msg = "Invalid password",
    )

class LoginUserNotFoundException :
    UserException(
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "User not found",
    )
