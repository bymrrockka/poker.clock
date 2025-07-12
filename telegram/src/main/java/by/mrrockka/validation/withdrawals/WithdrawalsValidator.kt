package by.mrrockka.validation.withdrawals

import by.mrrockka.domain.Person
import by.mrrockka.domain.TelegramPerson
import by.mrrockka.domain.collection.PersonEntries
import by.mrrockka.domain.collection.PersonWithdrawals
import by.mrrockka.domain.game.CashGame
import by.mrrockka.service.exception.EntriesForPersonNotFoundException
import org.springframework.stereotype.Component
import java.math.BigDecimal

//todo: refactor to kotlin
@Component
class WithdrawalsValidator {
    fun validateWithdrawalsAgainstEntries(
            personAndAmountMap: MutableMap<TelegramPerson?, BigDecimal>,
            cashGame: CashGame
    ) {
        val storedWithdrawals = cashGame.getWithdrawals()
        val totalWithdrawalsAmount = storedWithdrawals.stream()
                .map<BigDecimal> { obj: PersonWithdrawals? -> obj!!.total() }
                .reduce { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
                .orElse(BigDecimal.ZERO)
        val storedEntries = cashGame.getEntries()
        val totalEntriesAmount = storedEntries.stream()
                .map<BigDecimal> { obj: PersonEntries? -> obj!!.total() }
                .reduce { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
                .orElse(BigDecimal.ZERO)

        if (totalEntriesAmount.compareTo(totalWithdrawalsAmount) == 0) {
            throw InsufficientEntriesAmountException()
        }

        val requestedTotalWithdrawals = personAndAmountMap.values.stream()
                .reduce { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
                .orElse(BigDecimal.ZERO)

        if (requestedTotalWithdrawals.add(totalWithdrawalsAmount).compareTo(totalEntriesAmount) > 0) {
            throw InsufficientEntriesAmountException(totalEntriesAmount, totalWithdrawalsAmount)
        }

        val storedNicknames = storedEntries.stream()
                .map<Person?>(PersonEntries::person)
                .map<String?>(Person::nickname)
                .toList()

        val missed = personAndAmountMap.keys.stream()
                .map { it?.nickname }
                .filter { nickname: String? -> !storedNicknames.contains(nickname) }
                .findAny()

        if (missed.isPresent()) {
            throw EntriesForPersonNotFoundException(missed.get())
        }
    }
}
