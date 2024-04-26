package by.mrrockka.validation.withdrawals;

import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.TelegramPersonCreator;
import by.mrrockka.creator.WithdrawalsCreator;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.exception.BusinessException;
import by.mrrockka.service.exception.EntriesForPersonNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class WithdrawalsValidatorTest {

  private static final TelegramPerson TELEGRAM_PERSON = TelegramPersonCreator.domain();
  private final WithdrawalsValidator validator = new WithdrawalsValidator();


  private static Stream<Arguments> positiveScenarios() {
    return Stream.of(
//      case 1 Game has no withdrawals stored and requested withdrawal is equal to entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.TEN),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(Collections.emptyList()))
      ),
//      case 2 Game has withdrawals stored and requested withdrawal plus stored is equal to entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.valueOf(9)),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(WithdrawalsCreator.withdrawalsList(1, BigDecimal.ONE)))
      ),
//      case 3 Game has no withdrawals stored and requested withdrawal is less than entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.valueOf(9)),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(Collections.emptyList()))
      ),
//      case 4 Game has withdrawals stored and requested withdrawal plus stored is less than entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.valueOf(2)),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(WithdrawalsCreator.withdrawalsList(1, BigDecimal.ONE)))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("positiveScenarios")
  void givenValidWithdrawalMapAndCashGame_whenValidateWithdrawalsAgainstEntriesExecuted_thenShouldNotThrowException(
    final Map<TelegramPerson, BigDecimal> personAndAmountMap, final CashGame cashGame
  ) {
    assertThatCode(
      () -> validator.validateWithdrawalsAgainstEntries(personAndAmountMap, cashGame)).doesNotThrowAnyException();
  }

  private static Stream<Arguments> negativeScenarios() {
    return Stream.of(
//      case 1 Game has no withdrawals stored and requested withdrawal is more than entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.valueOf(100)),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(Collections.emptyList())),
        InsufficientEntriesAmountException.class
      ),
//      case 2 Game has withdrawals stored and requested withdrawal plus stored is more than entries total
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.TEN),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(WithdrawalsCreator.withdrawalsList(1, BigDecimal.ONE))),
        InsufficientEntriesAmountException.class
      ),
//      case 3 Game has withdrawals stored that equals entries total and requested contains a value
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.TEN),
        GameCreator.cash(builder -> builder
          .entries(
            List.of(EntriesCreator.entries(entry -> entry.person(TELEGRAM_PERSON).entries(List.of(BigDecimal.TEN)))))
          .withdrawals(WithdrawalsCreator.withdrawalsList(1, BigDecimal.TEN))),
        InsufficientEntriesAmountException.class
      ),
//      case 4 Request contains person that did not enter a game
      Arguments.of(
        Map.of(TELEGRAM_PERSON, BigDecimal.TEN),
        GameCreator.cash(builder -> builder
          .entries(EntriesCreator.entriesList(7, BigDecimal.TEN))
          .withdrawals(WithdrawalsCreator.withdrawalsList(1, BigDecimal.TEN))),
        EntriesForPersonNotFoundException.class
      )
    );
  }

  @ParameterizedTest
  @MethodSource("negativeScenarios")
  void givenInvalidWithdrawalMapAndCashGame_whenValidateWithdrawalsAgainstEntriesExecuted_thenShouldNotThrowException(
    final Map<TelegramPerson, BigDecimal> personAndAmountMap, final CashGame cashGame,
    final Class<BusinessException> exception
  ) {
    assertThatCode(() -> validator.validateWithdrawalsAgainstEntries(personAndAmountMap, cashGame))
      .isInstanceOf(exception);
  }

}