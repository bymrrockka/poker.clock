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
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface PollTaskRepo {
    fun store(task: PollTask)
    fun store(tasks: List<PollTask>)
    fun selectActive(): List<PollTask>
    fun finish(messageId: Long, finishedAt: Instant): Int
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PollTaskRepoImpl : PollTaskRepo {

    override fun store(task: PollTask) {
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

    override fun store(tasks: List<PollTask>) {
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

    override fun finish(messageId: Long, finishedAt: Instant): Int {
        return PollTaskTable
                .update({ PollTaskTable.messageId eq messageId }) {
                    it[PollTaskTable.finishedAt] = finishedAt
                }
    }

    override fun selectActive(): List<PollTask> {
        return PollTaskTable.selectAll()
                .where { finishedAt.isNull() }
                .map { it.toPollTask() }
    }

    private fun ResultRow.toPollTask() = PollTask(
            id = this[id],
            chatId = this[chatId],
            messageId = this[messageId],
            cron = CronExpression.parse(this[cron]),
            createdAt = this[createdAt],
            updatedAt = this[updatedAt],
            finishedAt = this[finishedAt],
            message = this[message],
            options = this[options].toList(),
    )
}