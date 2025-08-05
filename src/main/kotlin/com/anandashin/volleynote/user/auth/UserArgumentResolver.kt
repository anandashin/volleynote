package com.anandashin.volleynote.user.auth

import com.anandashin.volleynote.user.AuthenticationException
import com.anandashin.volleynote.user.dto.UserDTO
import com.anandashin.volleynote.user.service.UserService
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
    private val userService: UserService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserDTO::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): UserDTO? {
        return runCatching {
            val accessToken =
                requireNotNull(
                    webRequest.getHeader("Authorization")
                        ?.takeIf { it.startsWith("Bearer ") }
                        ?.substringAfter("Bearer ")
                )
            userService.authenticateUser(accessToken)
        }.getOrElse {
            if (parameter.hasParameterAnnotation(AuthUser::class.java)) {
                throw AuthenticationException()
            } else {
                null
            }
        }
    }
}
