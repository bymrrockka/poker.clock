package by.mrrockka.scenario.config

import by.mrrockka.PokerClockExceptionHandler
import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.domain.PokerClockBotOptions
import eu.vendeli.spring.starter.SpringClassManager
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import kotlinx.coroutines.DelicateCoroutinesApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates
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

    @OptIn(DelicateCoroutinesApi::class)
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
    open fun testBotOptions(): DefaultBotOptions {
        val botOptions = PokerClockBotOptions(
                updateTypes = listOf(
                        AllowedUpdates.EDITEDMESSAGE,
                        AllowedUpdates.MESSAGE,
                        AllowedUpdates.POLL,
                        AllowedUpdates.POLLANSWER,
                ),
        )
        botOptions.baseUrl = "$wiremockServerBaseUrl/"
        return botOptions
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