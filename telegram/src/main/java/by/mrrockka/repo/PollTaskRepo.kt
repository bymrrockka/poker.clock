package by.mrrockka.repo

import by.mrrockka.domain.PollTask
import by.mrrockka.repo.PollTaskTable.chatId
import by.mrrockka.repo.PollTaskTable.createdAt
import by.mrrockka.repo.PollTaskTable.cron
import by.mrrockka.repo.PollTaskTable.finishedAt
import by.mrrockka.repo.PollTaskTable.id
import by.mrrockka.repo.PollTaskTable.message
import by.mrrockka.repo.PollTaskTable.messageId
import by.mrrockka.repo.PollTaskTable.options
import by.mrrockka.repo.PollTaskTable.updatedAt
import org.jetbrains.exposed.sql.*
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PollTaskRepo {

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

    open fun batchUpsert(tasks: List<PollTask>) {
        PollTaskTable.batchInsert(tasks) { task ->
            this[id] = task.id
            this[chatId] = task.chatId
            this[messageId] = task.messageId
            this[createdAt] = task.createdAt
            this[updatedAt] = task.updatedAt
            this[finishedAt] = task.finishedAt
            this[message] = task.message
            this[cron] = task.cron.toString()
            this[options] = task.options.toTypedArray()
        }
    }

    open fun finishPoll(messageId: Int, finishedAt: Instant): Int {
        return PollTaskTable
                .update({ PollTaskTable.messageId eq messageId }) {
                    it[PollTaskTable.finishedAt] = finishedAt
                }
    }

    open fun selectNotFinished(): List<PollTask> {
        return PollTaskTable.selectAll()
                .where { finishedAt.isNull() }
                .map { pollTask(it) }
    }

    private fun pollTask(it: ResultRow) = PollTask(
            id = it[id],
            chatId = it[chatId],
            messageId = it[messageId],
            cron = CronExpression.parse(it[cron]),
            createdAt = it[createdAt],
            updatedAt = it[updatedAt],
            finishedAt = it[finishedAt],
            message = it[message],
            options = it[options].toList(),
    )
}