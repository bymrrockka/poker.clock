package by.mrrockka.domain

import eu.vendeli.tgbot.api.common.sendPoll
import eu.vendeli.tgbot.interfaces.action.Action
import eu.vendeli.tgbot.types.poll.PollType
import kotlinx.serialization.Serializable
import org.springframework.scheduling.support.CronExpression
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

interface Task {
    val id: UUID
    val cron: CronExpression
    val createdAt: Instant
    val updatedAt: Instant?
    val finishedAt: Instant?
    val chatId: Long

    fun toMessage(): Action<*>
    fun shouldBeExecuted(time: Instant): Boolean
}

data class PollTask(
        override val id: UUID,
        override val chatId: Long,
        val messageId: Long,
        override val cron: CronExpression,
        val message: String,
        val options: List<Option>,
        override val updatedAt: Instant? = null,
        override val finishedAt: Instant? = null,
        override val createdAt: Instant,
) : Task {
    @Serializable
    data class Option(
        val text: String,
        val participant: Boolean? = false,
    )

    override fun toMessage(): Action<*> {
        return sendPoll(message) {
            options.forEach {
                option { it.text }
            }
        }.options {
            isAnonymous = false
            type = PollType.Regular
        }
    }

    override fun shouldBeExecuted(time: Instant): Boolean {
        val now = time.toDateTime()
        val nextExecution = cron.next((updatedAt ?: createdAt).toDateTime())
        return finishedAt == null && nextExecution != null && nextExecution.isEqualsOrBefore(now)
    }
}

fun Instant.toDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
fun LocalDateTime.isEqualsOrBefore(time: LocalDateTime) = this.isBefore(time) || this.isEqual(time)