package by.mrrockka.repo

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

interface WithdrawalsRepo {
    fun findByPerson(gameId: UUID, personId: UUID): List<BigDecimal>
}

@Repository
@Transactional
open class WithdrawalsRepoImpl : WithdrawalsRepo {
    override fun findByPerson(gameId: UUID, personId: UUID): List<BigDecimal> {
        return WithdrawalTable.selectAll()
                .where {
                    (WithdrawalTable.personId eq personId) and
                            (WithdrawalTable.gameId eq gameId)
                }
                .map { it[WithdrawalTable.amount] }
    }

}