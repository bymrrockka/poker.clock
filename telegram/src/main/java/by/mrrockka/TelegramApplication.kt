package by.mrrockka

import eu.vendeli.spring.starter.TelegramAutoConfiguration
import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@ComponentScan("by.mrrockka", "org.telegram.telegrambots", "eu.vendeli")
@ImportAutoConfiguration(value = [ExposedAutoConfiguration::class, TelegramAutoConfiguration::class])
open class TelegramApplication {

    fun main(args: Array<String>) {
        runApplication<TelegramApplication>(*args)
    }
}
