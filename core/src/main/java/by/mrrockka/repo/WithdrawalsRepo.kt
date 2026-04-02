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

interface WithdrawalsRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant)
    fun update(gameId: UUID, personId: UUID, updatedAt: Instant, isDeleted: Boolean = false)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class WithdrawalsRepoImpl(
        val clock: Clock,
) : WithdrawalsRepo {
    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return WithdrawalTable.selectAll()
                .where { (WithdrawalTable.gameId eq gameId) and (WithdrawalTable.isDeleted neq true) }
                .map { it[WithdrawalTable.personId] to it[WithdrawalTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant) {
        WithdrawalTable.batchInsert(personIds) { personId ->
            this[WithdrawalTable.personId] = personId
            this[WithdrawalTable.amount] = amount
            this[WithdrawalTable.gameId] = gameId
            this[WithdrawalTable.createdAt] = createdAt
        }
    }

    override fun update(gameId: UUID, personId: UUID, updatedAt: Instant, isDeleted: Boolean) {
        WithdrawalTable.update(
                limit = 1,
                where = {
                    (WithdrawalTable.gameId eq gameId) and
                            (WithdrawalTable.personId eq personId) and
                            (WithdrawalTable.isDeleted neq true)
                },
        ) {
            it[WithdrawalTable.isDeleted] = isDeleted
            it[WithdrawalTable.updatedAt] = updatedAt
        }
    }

}