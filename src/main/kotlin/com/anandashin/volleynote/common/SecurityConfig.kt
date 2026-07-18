package com.anandashin.volleynote.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
open class SecurityConfig {
    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authz ->
                // 관리자 전용 경로 스켈레톤 (현재 비활성).
                // 활성화하려면 다음 중 하나가 선행돼야 함:
                //   (1) JwtAuthenticationFilter를 도입해 요청 필터 단계에서
                //       SecurityContext에 Authentication(role 포함)을 세팅, 그 뒤 hasRole 사용
                //   (2) 또는 컨트롤러 레벨에서 @AuthAdmin 같은 커스텀 리졸버로 role 검증
                // authz.requestMatchers("/api/admin/**").hasRole("ADMIN")
                authz.anyRequest().permitAll()
            }
        return http.build()
    }
}
