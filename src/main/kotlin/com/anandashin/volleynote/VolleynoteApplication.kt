package com.anandashin.volleynote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
open class VolleynoteApplication

fun main(args: Array<String>) {
    runApplication<VolleynoteApplication>(*args)
}
