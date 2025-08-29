package by.mrrockka.scenario.config

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.domain.PokerClockBotOptions
import eu.vendeli.spring.starter.SpringClassManager
import eu.vendeli.tgbot.TelegramBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates

@Profile("scenario")
@TestConfiguration
open class TestBotConfig(
        private val botProps: TelegramBotsProperties,
) {

    @Value("\${wiremock.server.baseUrl:}")
    lateinit var wiremockServerBaseUrl: String

    @Bean
    @Primary
    open fun bot(appContext: ApplicationContext): TelegramBot {
        return TelegramBot(botProps.token) {
//            classManager = SpringClassManager(appContext)
            apiHost = wiremockServerBaseUrl
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

}