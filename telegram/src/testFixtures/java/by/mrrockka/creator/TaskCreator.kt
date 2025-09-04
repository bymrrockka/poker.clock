package by.mrrockka.creator

import by.mrrockka.Random
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.PollTask.Option
import org.springframework.scheduling.support.CronExpression
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class TaskCreator {
    companion object {
        val poll = PollTask(
                id = UUID.randomUUID(),
                chatId = ChatCreator.CHAT_ID,
                messageId = MessageCreator.MESSAGE_ID.toLong(),
                cron = CronExpression.parse("* * * * * *"),
                message = "Poll message",
                options = listOf(Option("Yes", true), Option("No")),
                createdAt = MessageCreator.MESSAGE_TIMESTAMP.truncatedTo(ChronoUnit.SECONDS)
        )

        fun randomPoll(): PollTask {
            return poll.copy(
                    id = UUID.randomUUID(),
                    messageId = Random.messageId().toLong(),
                    chatId = Random.chatId(),
                    createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            )
        }
    }
}