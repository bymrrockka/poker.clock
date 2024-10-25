package by.mrrockka.repo.poll

import by.mrrockka.domain.PollTask
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb

object PollTaskTable : Table("poll_task") {
    val id = uuid("id")
    val chatId = long("chat_id")
    val messageId = integer("message_id").nullable()
    val cron = varchar("cron", 20)
    val createdAt = timestamp("created_at")
    val finishedAt = timestamp("finishedAt").nullable()
    val message = varchar("message", 255)
    val options = jsonb<Array<PollTask.Option>>("options", Json.Default)
}