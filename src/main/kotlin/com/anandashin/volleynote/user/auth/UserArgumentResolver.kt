package com.anandashin.volleynote.user.auth

import com.anandashin.volleynote.user.AuthenticationException
import com.anandashin.volleynote.user.dto.UserDTO
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserDTO::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserDTO? {
        val principal = SecurityContextHolder.getContext().authentication?.principal as? UserPrincipal
        if (principal == null) {
            if (parameter.hasParameterAnnotation(AuthUser::class.java)) {
                throw AuthenticationException()
            }
            return null
        }
        return UserDTO(
            id = principal.id,
            email = principal.email,
            nickname = principal.nickname,
            introduction = principal.introduction,
            role = principal.role,
        )
    }
}
