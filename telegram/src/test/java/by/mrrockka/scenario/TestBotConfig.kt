package by.mrrockka.scenario

import by.mrrockka.BotProperties
import by.mrrockka.PokerClockExceptionHandler
import by.mrrockka.SpringClassManager
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        private val botProps: BotProperties,
) {

    @Value("\${github.pipeline}")
    var githubPipeline: Boolean = false

    @Bean
    @Primary
    @OptIn(DelicateCoroutinesApi::class)
    open fun testBot(appContext: ApplicationContext, server: MockServer): TelegramBot {
        val bot = TelegramBot(botProps.token) {
            classManager = SpringClassManager(appContext)
            apiHost = server.server.url("").toString().dropLast(1)
            commandParsing {
                commandDelimiter = '\n'
                restrictSpacesInCommands = true
            }
            updatesListener {
                if (githubPipeline) {
                    pullingDelay = 80
                }
            }
            exceptionHandlingStrategy = ExceptionHandlingStrategy.Handle(PokerClockExceptionHandler)
        }
        GlobalScope.launch {
            bot.handleUpdates()
        }
        return bot
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
