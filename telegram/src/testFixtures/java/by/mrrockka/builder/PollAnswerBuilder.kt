package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.common.PollAnswer
import kotlin.time.ExperimentalTime

class PollAnswerBuilder(init: (PollAnswerBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    private var user: User? = null
    private var pollId: String? = null
    private var options: List<Int> = emptyList()

    fun user(user: User) {
        this.user = user
    }

    fun pollId(pollId: String) {
        this.pollId = pollId
    }

    fun option(index: Int) {
        this.options += index
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    @OptIn(ExperimentalTime::class)
    fun pollAnswer(): PollAnswer {
        return PollAnswer(
                pollId = pollId ?: error("Must have poll id"),
                user = user ?: user(),
                optionIds = options,
        )
    }
}

fun pollAnswer(init: (PollAnswerBuilder.() -> Unit) = {}) = PollAnswerBuilder(init).pollAnswer()
