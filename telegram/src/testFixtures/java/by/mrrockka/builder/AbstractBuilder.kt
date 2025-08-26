package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms

abstract class AbstractBuilder {
    internal var randoms = telegramRandoms

    fun randoms(randoms: TelegramRandoms) {
        this.randoms = randoms
    }
}