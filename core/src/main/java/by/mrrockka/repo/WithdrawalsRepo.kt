package by.mrrockka.repo

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface WithdrawalsRepo {
    fun findByPerson(gameId: UUID, personId: UUID): List<BigDecimal>
    fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class WithdrawalsRepoImpl : WithdrawalsRepo {
    override fun findByPerson(gameId: UUID, personId: UUID): List<BigDecimal> {
        return WithdrawalTable.selectAll()
                .where {
                    (WithdrawalTable.personId eq personId) and
                            (WithdrawalTable.gameId eq gameId)
                }
                .map { it[WithdrawalTable.amount] }
    }

    override fun store(gameId: UUID, personIds: List<UUID>, amount: BigDecimal, createdAt: Instant) {
        WithdrawalTable.batchInsert(personIds) { personId ->
            this[WithdrawalTable.personId] = personId
            this[WithdrawalTable.amount] = amount
            this[WithdrawalTable.gameId] = gameId
            this[WithdrawalTable.createdAt] = createdAt
        }
    }

}