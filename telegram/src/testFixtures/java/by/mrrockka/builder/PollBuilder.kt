package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.poll.Poll
import eu.vendeli.tgbot.types.poll.PollType
import kotlin.time.ExperimentalTime

class PollBuilder(init: (PollBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    private var id: String? = null
    private var question: String? = null

    fun id(id: String) {
        this.id = id
    }

    fun question(question: String) {
        this.question = question
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    @OptIn(ExperimentalTime::class)
    fun poll(): Poll {
        return Poll(
                id = id ?: randoms.pollid(),
                question = question ?: randoms.faker.backToTheFuture().quote(),
                options = emptyList(),
                totalVoterCount = 0,
                isClosed = false,
                isAnonymous = false,
                type = PollType.Regular,
                allowsMultipleAnswers = false,
        )
    }
}

fun poll(init: (PollBuilder.() -> Unit) = {}) = PollBuilder(init).poll()
