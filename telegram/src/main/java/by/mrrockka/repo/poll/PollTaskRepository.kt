package by.mrrockka.repo.poll

import by.mrrockka.domain.PollTask
import by.mrrockka.repo.poll.PollTaskTable.cron
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component


@Component
class PollTaskRepository {

    fun upsert(task: PollTask) {
        transaction {
            PollTaskTable.upsert {
                it[id] = task.id
                it[chatId] = task.chatId
                it[messageId] = task.messageId
                it[createdAt] = task.createdAt
                it[finishedAt] = task.finishedAt
                it[message] = task.message
                it[options] = task.options.toTypedArray()
            }
        }
    }

    fun selectNotFinished(): List<PollTask> {
        return transaction {
            PollTaskTable.selectAll()
                .where { PollTaskTable.finishedAt.isNull() }
                .map { pollTask(it) }
        }
    }

    private fun pollTask(it: ResultRow) = PollTask(
        id = it[PollTaskTable.id],
        chatId = it[PollTaskTable.chatId],
        messageId = it[PollTaskTable.messageId],
            cron = CronExpression.parse(it[cron]),
        createdAt = it[PollTaskTable.createdAt],
        finishedAt = it[PollTaskTable.finishedAt],
        message = it[PollTaskTable.message],
        options = it[PollTaskTable.options].toList(),
    )
}