package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.MoneyTransfer
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.payout.TransferType.CREDIT
import by.mrrockka.domain.payout.TransferType.DEBIT
import by.mrrockka.mapper.MoneyTransferMapper
import by.mrrockka.repo.moneytransfer.MoneyTransferRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
open class MoneyTransferService(
        val moneyTransferRepository: MoneyTransferRepository,
        val moneyTransferMapper: MoneyTransferMapper
) {

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    open fun storeBatch(game: Game, payouts: List<Payout>) {
        moneyTransferRepository.saveAll(payouts.mapToMoneyTransfers(game.id))
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    open fun storeBatch(game: by.mrrockka.domain.game.Game, payouts: MutableList<by.mrrockka.domain.payout.Payout?>) {
        moneyTransferRepository.saveAll(moneyTransferMapper.map(game.getId(), payouts), Instant.now())
    }

    fun getForPerson(person: Person): MutableList<MoneyTransfer> {
        return moneyTransferRepository.getForPerson(person.id).stream()
                .map<MoneyTransfer?> { moneyTransferMapper.map(it) }
                .toList()
    }

    private fun List<Payout>.mapToMoneyTransfers(gameId: UUID): List<MoneyTransfer> =
            flatMap {
                it.debtors.map { MoneyTransfer(it.player.person.id, gameId, it.debt, DEBIT) } +
                        MoneyTransfer(it.creditor.person.id, gameId, it.total, CREDIT)
            }

}
