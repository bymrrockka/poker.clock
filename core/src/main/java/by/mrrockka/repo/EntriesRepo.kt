package by.mrrockka.repo

import by.mrrockka.domain.Game
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface EntriesRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun store(personIds: List<UUID>, amount: BigDecimal, game: Game, createdAt: Instant)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class EntriesRepoImpl : EntriesRepo {

    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { EntriesTable.gameId eq gameId }
                .map { it[EntriesTable.personId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(personIds: List<UUID>, amount: BigDecimal, game: Game, createdAt: Instant) {
        EntriesTable.batchInsert(personIds) { id ->
            this[EntriesTable.gameId] = game.id
            this[EntriesTable.personId] = id
            this[EntriesTable.amount] = amount
            this[EntriesTable.createdAt] = createdAt
        }
    }
}