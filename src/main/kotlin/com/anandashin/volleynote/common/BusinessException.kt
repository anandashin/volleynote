package com.anandashin.volleynote.common

open class BusinessException(
    val errorCode: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(errorCode.message, cause)
