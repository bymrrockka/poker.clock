package by.mrrockka.domain

import org.telegram.telegrambots.bots.DefaultBotOptions

data class PokerClockBotOptions(
        val updateTypes: List<String>,
) : DefaultBotOptions() {
    init {
        this.setAllowedUpdates(updateTypes)
    }
}
