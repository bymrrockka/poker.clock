package by.mrrockka.repo

import by.mrrockka.domain.Player
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Repository
@Transactional
open class EntriesRepo {

    fun findGameEntries(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { EntriesTable.gameId eq gameId }
                .map { it[EntriesTable.personId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    fun upsert(player: Player, gameId: UUID, instant: java.time.Instant) {
        EntriesTable.batchInsert(player.entries) {
            this[EntriesTable.gameId] = gameId
            this[EntriesTable.personId] = player.person.id
            this[EntriesTable.amount] = it
            this[EntriesTable.createdAt] = instant
        }
    }
}