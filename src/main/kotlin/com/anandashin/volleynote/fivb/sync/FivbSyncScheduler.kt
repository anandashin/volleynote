package com.anandashin.volleynote.fivb.sync

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.Year

// 주기적 FIVB 동기화. fivb.sync.enabled=true 일 때만 활성(기본 꺼짐 → 테스트/dev에서 외부 호출 안 함).
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "fivb.sync", name = ["enabled"], havingValue = "true")
open class FivbSyncScheduler(
    private val fivbSyncService: FivbSyncService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // 기본 매일 04:00. fivb.sync.cron 으로 재정의 가능.
    @Scheduled(cron = "\${fivb.sync.cron:0 0 4 * * *}")
    open fun scheduledSync() {
        val season = Year.now().value
        log.info("scheduled FIVB sync start (season={})", season)
        fivbSyncService.sync(season)
    }
}
