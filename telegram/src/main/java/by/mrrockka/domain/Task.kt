package by.mrrockka.domain

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

interface Task {
    val id: UUID
    val cron: String
    val createdAt: Instant
    val finishedAt: Instant?
    val chatId: Long

    fun toMessage(): BotApiMethodMessage
    fun shouldBeExecuted(time: LocalDateTime): Boolean
}

data class PollTask(
    override val id: UUID,
    override val chatId: Long,
    val messageId: Int,
    override val cron: String,
    val message: String,
    val options: List<Option>,
    override val finishedAt: Instant? = null,
    override val createdAt: Instant
) : Task {
    data class Option(
        val text: String,
        val isParticipant: Boolean? = false,
    )

    override fun toMessage(): BotApiMethodMessage {
        return SendPoll().also {
            it.chatId = chatId.toString()
            it.question = message
            it.options = options.map(Option::text)
        }
    }

    override fun shouldBeExecuted(time: LocalDateTime): Boolean {
        TODO("Not implemented")
    }
}

data class ForcedBetsTask(
    override val id: UUID,
    override val cron: String,
    override val chatId: Long,
    val schema: Schema,
    override val createdAt: Instant,
    override val finishedAt: Instant? = null,
) : Task {
    var currentIndex: AtomicInteger = AtomicInteger(0)

    data class Schema(
        val id: UUID,
        val forcedBets: List<ForcedBets>
    )

    data class ForcedBets(
        val index: Int,
        val bigBlind: BigDecimal,
        val smallBlind: BigDecimal,
        val ante: BigDecimal
    )

    override fun toMessage(): BotApiMethodMessage {
//        todo: get blinds for current iteration
        currentIndex.getAndIncrement()
//        todo: send message
        TODO("Not yet implemented")
    }


    override fun shouldBeExecuted(time: LocalDateTime): Boolean {
        TODO("Not implemented")
    }
}

