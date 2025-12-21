package by.mrrockka.repo

import by.mrrockka.domain.GameType
import by.mrrockka.domain.PositionPrize
import by.mrrockka.domain.TransferType
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb

private val objectMapper = jsonMapper {
    addModule(kotlinModule())
    serializationInclusion(JsonInclude.Include.NON_NULL)
}

object GameTable : Table("game") {
    val id = uuid("id")
    val gameType = enumerationByName<GameType>("game_type", 15)
    val buyIn = decimal("buy_in", 20, 2)
    val stack = decimal("stack", 20, 2).nullable()
    val bounty = decimal("bounty", 20, 2).nullable()
    val createdAt = timestamp("created_at")
    val finishedAt = timestamp("finished_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object PersonTable : Table("person") {
    val id = uuid("id")
    val firstName = varchar("first_name", 100).nullable()
    val lastName = varchar("last_name", 100).nullable()
    val nickName = varchar("nick_name", 32).nullable()

    override val primaryKey = PrimaryKey(id)
}

object EntriesTable : Table("entries") {
    val gameId = uuid("game_id").references(GameTable.id)
    val personId = uuid("person_id").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object WithdrawalTable : Table("withdrawal") {
    val gameId = uuid("game_id").references(GameTable.id)
    val personId = uuid("person_id").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object BountyTable : Table("bounty") {
    val gameId = uuid("game_id").references(GameTable.id)
    val from_person = uuid("from_person").references(PersonTable.id)
    val to_person = uuid("to_person").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object PrizePoolTable : Table("prize_pool") {
    val gameId = uuid("game_id").references(GameTable.id)
    val schema = jsonb("schema", { objectMapper.writeValueAsString(it) }, { objectMapper.readValue<Array<PositionPrize>>(it) })

    override val primaryKey = PrimaryKey(gameId)
}

object FinalePlacesTable : Table("finale_places") {
    val gameId = uuid("game_id").references(GameTable.id)
    val personId = uuid("person_id").references(PersonTable.id)
    val position = integer("position")
}

object MoneyTransferTable : Table("money_transfer") {
    val gameId = uuid("game_id").references(GameTable.id)
    val personId = uuid("person_id").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val type = enumerationByName<TransferType>("type", 6)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

object GameSummaryTable : Table("game_summary") {
    val gameId = uuid("game_id").references(GameTable.id)
    val personId = uuid("person_id").references(PersonTable.id)
    val position = integer("position").nullable()
    val amount = decimal("amount", 20, 2)
    val type = varchar("type", 20)
}

