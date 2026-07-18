package com.anandashin.volleynote.user.auth

import com.anandashin.volleynote.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token =
            request.getHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer ") }
                ?.substringAfter("Bearer ")

        if (token != null) {
            val userId = JwtTokenProvider.validateTokenAndGetUserId(token)
            if (userId != null) {
                val user = userRepository.findByIdOrNull(userId)
                if (user != null) {
                    val principal =
                        UserPrincipal(
                            id = user.id,
                            email = user.email,
                            nickname = user.nickname,
                            introduction = user.introduction,
                            role = user.role,
                        )
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
                    val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
