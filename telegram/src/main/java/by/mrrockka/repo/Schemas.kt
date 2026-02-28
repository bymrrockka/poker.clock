package by.mrrockka.repo

import by.mrrockka.domain.PollTask
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.json.jsonb

object PollTaskTable : Table("poll_task") {
    val id = javaUUID("id")
    val chatId = long("chat_id")
    val messageId = long("message_id")
    val cron = varchar("cron", 20)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    val finishedAt = timestamp("finished_at").nullable()
    val message = varchar("message", 255)
    val options = jsonb<Array<PollTask.Option>>("options", Json.Default)

    override val primaryKey = PrimaryKey(id)
}

object ChatPersonsTable : Table("chat_persons") {
    val personId = javaUUID("person_id").references(PersonTable.id)
    val chatId = long("chat_id")
}

object ChatGameTable : Table("chat_games") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val messageId = long("message_id")
    val chatId = long("chat_id")
    val createdAt = timestamp("created_at")
}

object ChatPollsTable : Table("chat_polls") {
    val pollId = javaUUID("poll_id").references(PollTaskTable.id)
    val tgPollId = varchar("telegram_poll_id", 50)
}

object PollAnswersTable : Table("poll_answers") {
    val pollId = varchar("telegram_poll_id", 50)
    val personId = javaUUID("person_id").references(PersonTable.id)
    val answer = integer("answer")
}

object PinMessageTable : Table("pin_messages") {
    val chatId = long("chat_id")
    val messageId = long("message_id")
    val type = enumerationByName<PinType>("type", 15)
}

object GameTablesTable : Table("game_tables") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val tables = jsonb<Array<by.mrrockka.domain.Table>>("tables", Json.Default)
}
