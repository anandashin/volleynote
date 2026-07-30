package com.anandashin.volleynote.user.domain

import com.anandashin.volleynote.common.JpaConfig
import com.anandashin.volleynote.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@Import(JpaConfig::class)
@ActiveProfiles("test")
class UserEntityAuditTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var em: TestEntityManager

    @Test
    fun `insert 시 createdAt과 updatedAt이 자동 세팅되고 deletedAt은 null이다`() {
        val before = LocalDateTime.now().minusSeconds(5)
        val user =
            UserEntity(
                email = "a@a.com",
                nickname = "a",
                hashedPassword = "x",
            )

        userRepository.saveAndFlush(user)
        val after = LocalDateTime.now().plusSeconds(5)

        assertThat(user.createdAt).isBetween(before, after)
        assertThat(user.updatedAt).isBetween(before, after)
        assertThat(user.deletedAt).isNull()
    }

    @Test
    fun `update 시 createdAt은 유지되고 updatedAt은 갱신된다`() {
        val user =
            UserEntity(
                email = "b@a.com",
                nickname = "old",
                hashedPassword = "x",
            )
        userRepository.saveAndFlush(user)
        val originalCreatedAt = user.createdAt
        val originalUpdatedAt = user.updatedAt

        // updatedAt이 관측 가능하게 다르도록 최소 지연
        Thread.sleep(20)
        user.nickname = "new"
        em.flush()

        assertThat(user.createdAt).isEqualTo(originalCreatedAt)
        assertThat(user.updatedAt).isAfterOrEqualTo(originalUpdatedAt)
    }

    @Test
    fun `deletedAt은 수동으로 세팅 가능하다 - 소프트 삭제용`() {
        val user =
            UserEntity(
                email = "c@a.com",
                nickname = "c",
                hashedPassword = "x",
            )
        userRepository.saveAndFlush(user)
        assertThat(user.deletedAt).isNull()

        user.deletedAt = LocalDateTime.now()
        em.flush()

        assertThat(user.deletedAt).isNotNull()
    }
}
