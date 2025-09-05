package by.mrrockka.domain

import org.telegram.telegrambots.bots.DefaultBotOptions

@Deprecated("For removal")
data class PokerClockBotOptions(
        val updateTypes: List<String>,
) : DefaultBotOptions() {
    init {
        this.setAllowedUpdates(updateTypes)
    }
}
