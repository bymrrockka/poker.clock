package by.mrrockka

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAspectJAutoProxy
@EnableScheduling
@ImportAutoConfiguration(value = [ExposedAutoConfiguration::class])
@SpringBootApplication
open class TelegramApplication {

    fun main(args: Array<String>) {
        runApplication<TelegramApplication>(*args)
    }
}
