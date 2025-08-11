package by.mrrockka.config

import by.mrrockka.domain.PokerClockBotOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates

@Configuration
open class BotConfig {
    @Bean
    open fun botOptions(): DefaultBotOptions {
        val botOptions = PokerClockBotOptions(
                updateTypes = listOf(
                        AllowedUpdates.EDITEDMESSAGE,
                        AllowedUpdates.MESSAGE,
                        AllowedUpdates.POLL,
                        AllowedUpdates.POLLANSWER,
                ),
        )
        return botOptions
    }
}