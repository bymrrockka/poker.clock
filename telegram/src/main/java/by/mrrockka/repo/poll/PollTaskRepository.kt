package by.mrrockka.repo.poll

import by.mrrockka.domain.PollTask
import by.mrrockka.repo.poll.PollTaskTable.cron
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
open class PollTaskRepository {

    @Transactional(propagation = Propagation.REQUIRED)
    open fun upsert(task: PollTask) {
        PollTaskTable.upsert {
            it[id] = task.id
            it[chatId] = task.chatId
            it[messageId] = task.messageId
            it[createdAt] = task.createdAt
            it[updatedAt] = task.updatedAt
            it[finishedAt] = task.finishedAt
            it[message] = task.message
            it[cron] = task.cron.toString()
            it[options] = task.options.toTypedArray()
        }
    }

    @Transactional
    open fun batchUpsert(tasks: List<PollTask>) {
        tasks.forEach { upsert(it) }
    }

    @Transactional
    open fun selectNotFinished(): List<PollTask> {
        return PollTaskTable.selectAll()
                .where { PollTaskTable.finishedAt.isNull() }
                .map { pollTask(it) }
    }

    private fun pollTask(it: ResultRow) = PollTask(
            id = it[PollTaskTable.id],
            chatId = it[PollTaskTable.chatId],
            messageId = it[PollTaskTable.messageId],
            cron = CronExpression.parse(it[cron]),
            createdAt = it[PollTaskTable.createdAt],
            updatedAt = it[PollTaskTable.updatedAt],
            finishedAt = it[PollTaskTable.finishedAt],
            message = it[PollTaskTable.message],
            options = it[PollTaskTable.options].toList(),
    )
}