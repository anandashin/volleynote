package com.anandashin.volleynote.user

import com.anandashin.volleynote.common.BusinessException
import com.anandashin.volleynote.common.ErrorCode

class SignUpEmailConflictException : BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS)

class LoginInvalidPasswordException : BusinessException(ErrorCode.INVALID_PASSWORD)

class LoginUserNotFoundException : BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND)

class AuthenticationException : BusinessException(ErrorCode.AUTHENTICATION_REQUIRED)

class UserNotFoundException : BusinessException(ErrorCode.USER_NOT_FOUND)
