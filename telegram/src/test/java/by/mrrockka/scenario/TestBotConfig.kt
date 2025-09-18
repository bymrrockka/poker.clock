package by.mrrockka.scenario

import by.mrrockka.PokerClockExceptionHandler
import by.mrrockka.SpringClassManager
import by.mrrockka.TelegramBotsProperties
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Profile("scenario")
@TestConfiguration
open class TestBotConfig(
        private val botProps: TelegramBotsProperties,
) {

    @Value("\${wiremock.server.baseUrl:}")
    lateinit var wiremockServerBaseUrl: String

    @Bean
    @Primary
    open fun testBot(appContext: ApplicationContext): TelegramBot {
        return TelegramBot(botProps.token) {
            classManager = SpringClassManager(appContext)
            apiHost = wiremockServerBaseUrl
            commandParsing {
                commandDelimiter = '\n'
                restrictSpacesInCommands = true
            }
            exceptionHandlingStrategy = ExceptionHandlingStrategy.Handle(PokerClockExceptionHandler)
        }
    }

    @Bean
    @Primary
    @OptIn(ExperimentalTime::class)
    open fun testClock(): TestClock {
        return TestClock()
    }
}

@OptIn(ExperimentalTime::class)
class TestClock : Clock {
    var time = Clock.System.now()
    override fun now(): Instant {
        return time
    }

    fun set(time: Instant) {
        this.time = time
    }
}