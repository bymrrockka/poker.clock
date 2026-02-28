package by.mrrockka

import org.jetbrains.exposed.v1.spring.boot4.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableConfigurationProperties
@ImportAutoConfiguration(value = [ExposedAutoConfiguration::class])
@SpringBootApplication
open class TelegramApplication

fun main(args: Array<String>) {
    runApplication<TelegramApplication>(*args)
}

