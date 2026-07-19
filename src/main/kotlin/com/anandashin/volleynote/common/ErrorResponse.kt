package com.anandashin.volleynote.common

data class ErrorResponse(
    val code: String,
    val message: String,
    val status: Int,
    val errors: List<FieldError>? = null,
) {
    data class FieldError(
        val field: String,
        val message: String?,
    )

    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse =
            ErrorResponse(
                code = errorCode.code,
                message = errorCode.message,
                status = errorCode.status.value(),
            )

        fun of(
            errorCode: ErrorCode,
            fieldErrors: List<FieldError>,
        ): ErrorResponse =
            ErrorResponse(
                code = errorCode.code,
                message = errorCode.message,
                status = errorCode.status.value(),
                errors = fieldErrors,
            )
    }
}
