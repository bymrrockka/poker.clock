package by.mrrockka

import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import org.junit.jupiter.api.AfterEach

abstract class AbstractTest {

    @AfterEach
    fun afterEach() {
        telegramRandoms.reset()
    }
}