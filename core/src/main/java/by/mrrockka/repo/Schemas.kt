package by.mrrockka.repo

import by.mrrockka.domain.GameType
import by.mrrockka.domain.PositionPrize
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.json.jsonb

object GameTable : Table("game") {
    val id = javaUUID("id")
    val gameType = enumerationByName<GameType>("game_type", 15)
    val buyIn = decimal("buy_in", 20, 2)
    val stack = decimal("stack", 20, 2).nullable()
    val bounty = decimal("bounty", 20, 2).nullable()
    val createdAt = timestamp("created_at")
    val finishedAt = timestamp("finished_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object PersonTable : Table("person") {
    val id = javaUUID("id")
    val firstName = varchar("first_name", 100).nullable()
    val lastName = varchar("last_name", 100).nullable()
    val nickName = varchar("nick_name", 32).nullable()

    override val primaryKey = PrimaryKey(id)
}

object EntriesTable : Table("entries") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val personId = javaUUID("person_id").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object WithdrawalTable : Table("withdrawal") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val personId = javaUUID("person_id").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object BountyTable : Table("bounty") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val from_person = javaUUID("from_person").references(PersonTable.id)
    val to_person = javaUUID("to_person").references(PersonTable.id)
    val amount = decimal("amount", 20, 2)
    val createdAt = timestamp("created_at")
}

object PrizePoolTable : Table("prize_pool") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val schema = jsonb<Array<PositionPrize>>("schema", Json.Default)

    override val primaryKey = PrimaryKey(gameId)
}

object FinalePlacesTable : Table("finale_places") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val personId = javaUUID("person_id").references(PersonTable.id)
    val position = integer("position")
}

object GameSummaryTable : Table("game_summary") {
    val gameId = javaUUID("game_id").references(GameTable.id)
    val personId = javaUUID("person_id").references(PersonTable.id)
    val position = integer("position").nullable()
    val buyIn = decimal("buyin", 20, 2)
    val entriesNum = integer("entries_num")
    val prize = decimal("prize", 20, 2).nullable()
    val withdrawals = decimal("withdrawals", 20, 2).nullable()
    val bounty = decimal("bounty", 20, 2).nullable()
    val takenNum = integer("taken_num").nullable()
    val givenNum = integer("given_num").nullable()
    val type = varchar("type", 20)
}

