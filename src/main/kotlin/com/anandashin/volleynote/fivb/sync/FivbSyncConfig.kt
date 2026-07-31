package com.anandashin.volleynote.fivb.sync

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
open class FivbSyncConfig {
    // FIVB VIS 웹서비스 전용 RestClient. Request XML은 쿼리 파라미터로 전달.
    @Bean
    open fun fivbRestClient(builder: RestClient.Builder): RestClient =
        builder
            .baseUrl("https://www.fivb.org/Vis2009/XmlRequest.asmx")
            .build()
}
