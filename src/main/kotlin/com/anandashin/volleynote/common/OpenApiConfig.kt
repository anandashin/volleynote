package com.anandashin.volleynote.common

import com.anandashin.volleynote.user.auth.AuthUser
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// springdoc-openapi 설정: 문서 메타 + JWT Bearer 인증 스킴.
// Swagger UI: /swagger-ui.html, OpenAPI JSON: /v3/api-docs
@Configuration
open class OpenApiConfig {
    init {
        // @AuthUser(UserDTO)는 인증 컨텍스트에서 주입되므로 요청 파라미터로 문서화하지 않음
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthUser::class.java)
    }

    @Bean
    open fun openAPI(): OpenAPI {
        val schemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("VolleyNote API")
                    .version("v1")
                    .description(
                        "여자 배구(FIVB 여자 국가대표) 관전 일지 커뮤니티 백엔드 API.\n\n" +
                            "대부분의 엔드포인트는 인증이 필요합니다. `/api/users/login`으로 받은 " +
                            "JWT를 우측 상단 **Authorize** 에 입력하면 이후 요청에 자동 첨부됩니다.",
                    ),
            )
            // 전역 기본: JWT 필요 (signup/login 등 permitAll은 개별적으로 인증 없이도 호출 가능)
            .addSecurityItem(SecurityRequirement().addList(schemeName))
            .components(
                Components().addSecuritySchemes(
                    schemeName,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"),
                ),
            )
    }
}
