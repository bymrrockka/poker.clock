package by.mrrockka.config

import by.mrrockka.domain.PokerClockBotOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.updates.AllowedUpdates
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Configuration
open class BotConfig {
    @Bean
    @Deprecated("Remove when old framework reduced")
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

    @Bean
    @OptIn(ExperimentalTime::class)
    open fun clock(): Clock {
        return Clock.System
    }
}