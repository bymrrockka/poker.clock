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

interface WithdrawalsRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun update(operationId: Int, updatedAt: Instant, isDeleted: Boolean)
    fun store(gameId: UUID, personId: UUID, amount: BigDecimal, createdAt: Instant): Int
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class WithdrawalsRepoImpl : WithdrawalsRepo {
    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return WithdrawalTable.selectAll()
                .where { (WithdrawalTable.gameId eq gameId) and (WithdrawalTable.isDeleted neq true) }
                .map { it[WithdrawalTable.personId] to it[WithdrawalTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(gameId: UUID, personId: UUID, amount: BigDecimal, createdAt: Instant): Int =
            WithdrawalTable.insertReturning {
                it[WithdrawalTable.gameId] = gameId
                it[WithdrawalTable.personId] = personId
                it[WithdrawalTable.amount] = amount
                it[WithdrawalTable.createdAt] = createdAt
            }.single()[WithdrawalTable.operationId]

    override fun update(operationId: Int, updatedAt: Instant, isDeleted: Boolean) {
        WithdrawalTable.update(
                where = {
                    WithdrawalTable.operationId eq operationId
                },
        ) {
            it[WithdrawalTable.isDeleted] = isDeleted
            it[WithdrawalTable.updatedAt] = updatedAt
        }
    }

}