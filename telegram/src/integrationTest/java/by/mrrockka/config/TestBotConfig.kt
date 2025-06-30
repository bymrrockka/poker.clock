package by.mrrockka.config

import by.mrrockka.domain.PokerClockBotOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates

@Profile("integration")
@TestConfiguration
open class TestBotConfig {

    @Value("\${wiremock.server.baseUrl:}")
    lateinit var wiremockServerBaseUrl: String

    @Bean
    @Primary
    open fun testBotOptions(): DefaultBotOptions {
        val botOptions = PokerClockBotOptions(
                listOf(AllowedUpdates.EDITEDMESSAGE,
                        AllowedUpdates.MESSAGE,
                        AllowedUpdates.POLL,
                        AllowedUpdates.POLLANSWER))
        botOptions.baseUrl = "$wiremockServerBaseUrl/"
        return botOptions
    }

}