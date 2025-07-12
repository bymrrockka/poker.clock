package by.mrrockka.validation.withdrawals;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.service.exception.EntriesForPersonNotFoundException;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class WithdrawalsValidator {

  public void validateWithdrawalsAgainstEntries(@NonNull final Map<TelegramPerson, BigDecimal> personAndAmountMap,
                                                @NonNull final CashGame cashGame) {
    final var storedWithdrawals = cashGame.getWithdrawals();
    final var totalWithdrawalsAmount = storedWithdrawals.stream()
      .map(PersonWithdrawals::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
    final var storedEntries = cashGame.getEntries();
    final var totalEntriesAmount = storedEntries.stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (totalEntriesAmount.compareTo(totalWithdrawalsAmount) == 0) {
      throw new InsufficientEntriesAmountException();
    }

    final var requestedTotalWithdrawals = personAndAmountMap.values().stream()
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (requestedTotalWithdrawals.add(totalWithdrawalsAmount).compareTo(totalEntriesAmount) > 0) {
      throw new InsufficientEntriesAmountException(totalEntriesAmount, totalWithdrawalsAmount);
    }

    final var storedNicknames = storedEntries.stream()
      .map(PersonEntries::person)
      .map(Person::getNickname)
      .toList();

    final var missed = personAndAmountMap.keySet().stream()
      .map(TelegramPerson::getNickname)
      .filter(nickname -> !storedNicknames.contains(nickname))
      .findAny();

    if (missed.isPresent()) {
      throw new EntriesForPersonNotFoundException(missed.get());
    }
  }
}
