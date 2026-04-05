package by.mrrockka.repo

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface EntriesRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant)
    fun store(gameId: UUID, personId: UUID, amount: BigDecimal, createdAt: Instant): Int
    fun update(operationId: Int, updatedAt: Instant, isDeleted: Boolean)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class EntriesRepoImpl : EntriesRepo {

    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { (EntriesTable.gameId eq gameId) and (EntriesTable.isDeleted neq true) }
                .map { it[EntriesTable.personId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant) {
        personIds.map { id -> store(gameId, id, amount, createdAt) }
    }

    override fun store(gameId: UUID, personId: UUID, amount: BigDecimal, createdAt: Instant): Int =
            EntriesTable.insertReturning {
                it[EntriesTable.gameId] = gameId
                it[EntriesTable.personId] = personId
                it[EntriesTable.amount] = amount
                it[EntriesTable.createdAt] = createdAt
            }.single()[EntriesTable.operationId]

    override fun update(operationId: Int, updatedAt: Instant, isDeleted: Boolean) {
        EntriesTable.update(
                where = { EntriesTable.operationId eq operationId },
        ) {
            it[EntriesTable.isDeleted] = isDeleted
            it[EntriesTable.updatedAt] = updatedAt
        }
    }

}