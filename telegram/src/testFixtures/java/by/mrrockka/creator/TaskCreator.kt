package by.mrrockka.creator

import by.mrrockka.domain.PollTask
import by.mrrockka.domain.PollTask.Option
import org.springframework.scheduling.support.CronExpression
import java.time.temporal.ChronoUnit
import java.util.*

final class TaskCreator {
    private constructor()

    companion object {
        val poll = PollTask(
                id = UUID.randomUUID(),
                chatId = ChatCreator.CHAT_ID,
                messageId = MessageCreator.MESSAGE_ID,
                cron = CronExpression.parse("* * 19 * * Wed"),
                message = "Poll message",
                options = listOf(Option("Yes", true), Option("No")),
                createdAt = MessageCreator.MESSAGE_TIMESTAMP.truncatedTo(ChronoUnit.SECONDS)
        )
    }
}