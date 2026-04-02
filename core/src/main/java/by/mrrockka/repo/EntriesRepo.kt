package by.mrrockka.repo

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.time.Clock

interface EntriesRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant)
    fun update(gameId: UUID, personId: UUID, updatedAt: Instant, isDeleted: Boolean = false)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class EntriesRepoImpl(
        private val clock: Clock,
) : EntriesRepo {

    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { (EntriesTable.gameId eq gameId) and (EntriesTable.isDeleted neq true) }
                .map { it[EntriesTable.personId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant) {
        EntriesTable.batchInsert(personIds) { id ->
            this[EntriesTable.gameId] = gameId
            this[EntriesTable.personId] = id
            this[EntriesTable.amount] = amount
            this[EntriesTable.createdAt] = createdAt
        }
    }

    override fun update(gameId: UUID, personId: UUID, updatedAt: Instant, isDeleted: Boolean) {
        EntriesTable.update(
                limit = 1,
                where = {
                    (EntriesTable.gameId eq gameId) and
                            (EntriesTable.personId eq personId) and
                            (EntriesTable.isDeleted neq true)
                },
        ) {
            it[EntriesTable.isDeleted] = isDeleted
            it[EntriesTable.updatedAt] = updatedAt
        }
    }

}