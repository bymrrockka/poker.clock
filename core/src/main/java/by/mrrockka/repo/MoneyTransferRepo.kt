package by.mrrockka.repo

import by.mrrockka.domain.Game
import by.mrrockka.domain.MoneyTransfer
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.TransferType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface MoneyTransferRepo {
    fun findByPerson(person: Person): List<MoneyTransfer>
    fun store(game: Game, payouts: List<Payout>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class MoneyTransferRepoImpl : MoneyTransferRepo {

    override fun findByPerson(person: Person): List<MoneyTransfer> {
        return MoneyTransferTable.selectAll()
                .where(MoneyTransferTable.personId eq person.id)
                .map { it.toMoneyTransfer() }
                .toList()
    }

    override fun store(game: Game, payouts: List<Payout>) {
        payouts.flatMap { payout ->
            payout.debtors.map {
                MoneyTransfer(
                        gameId = game.id,
                        personId = it.player.person.id,
                        amount = it.debt,
                        type = TransferType.DEBIT,
                )
            } + MoneyTransfer(
                    gameId = game.id,
                    personId = payout.creditor.person.id,
                    amount = payout.total,
                    type = TransferType.CREDIT,
            )
        }.also { moneyTransfers ->
            MoneyTransferTable.batchUpsert(
                    data = moneyTransfers,
                    keys = arrayOf(MoneyTransferTable.gameId, MoneyTransferTable.personId),
                    onUpdateExclude = listOf(MoneyTransferTable.createdAt),
            ) {
                this[MoneyTransferTable.gameId] = it.gameId
                this[MoneyTransferTable.personId] = it.personId
                this[MoneyTransferTable.amount] = it.amount
                this[MoneyTransferTable.type] = it.type
                this[MoneyTransferTable.createdAt] = Instant.now()
                this[MoneyTransferTable.updatedAt] = Instant.now()
            }
        }
    }

    private fun ResultRow.toMoneyTransfer(): MoneyTransfer {
        return MoneyTransfer(
                gameId = this[MoneyTransferTable.gameId],
                personId = this[MoneyTransferTable.personId],
                amount = this[MoneyTransferTable.amount],
                type = this[MoneyTransferTable.type],
        )
    }

}