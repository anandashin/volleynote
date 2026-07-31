package com.anandashin.volleynote.common

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiDocsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `OpenAPI 문서는 인증 없이 접근 가능하고 메타·경로·보안스킴을 포함`() {
        mockMvc
            .perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.info.title").value("VolleyNote API"))
            // 컨트롤러 경로가 문서에 포함되는지
            .andExpect(jsonPath("$.paths.['/api/notes']").exists())
            .andExpect(jsonPath("$.paths.['/api/fivb/tournaments']").exists())
            // JWT Bearer 보안 스킴 등록 확인
            .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
    }
}
