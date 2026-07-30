package com.anandashin.volleynote.common

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

// FIVB 미러처럼 저빈도 변경·고빈도 조회 데이터용 Caffeine 캐시.
@Configuration
@EnableCaching
open class CacheConfig {
    @Bean
    open fun cacheManager(): CacheManager {
        val manager = CaffeineCacheManager()
        manager.setCaffeine(
            Caffeine
                .newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1_000),
        )
        return manager
    }
}
