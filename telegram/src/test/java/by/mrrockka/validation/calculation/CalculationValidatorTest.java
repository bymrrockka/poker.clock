package by.mrrockka.validation.calculation;

import by.mrrockka.creator.BountyCreator;
import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.WithdrawalsCreator;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculationValidatorTest {

  private final CalculationValidator calculationValidator = new CalculationValidator();

  private static Stream<Arguments> validGames() {
    return Stream.of(
      Arguments.of(GameCreator.cash()),
      Arguments.of(GameCreator.tournament()),
      Arguments.of(GameCreator.bounty())
    );
  }

  @ParameterizedTest
  @MethodSource("validGames")
  void givenValidGame_whenValidateGameExecuted_thenShouldNotThrowExceptions(final Game game) {
    assertThatCode(() -> calculationValidator.validateGame(game)).doesNotThrowAnyException();
  }

  private static Stream<Arguments> invalidCashGames() {
    return Stream.of(
      Arguments.of(GameCreator.cash(builder -> builder
        .entries(EntriesCreator.entriesList(5, BigDecimal.TEN))
        .withdrawals(WithdrawalsCreator.withdrawalsList(4, BigDecimal.TEN)))),
      Arguments.of(GameCreator.cash(builder -> builder
        .entries(EntriesCreator.entriesList(5, BigDecimal.TEN))
        .withdrawals(WithdrawalsCreator.withdrawalsList(4, BigDecimal.valueOf(100)))))
    );
  }

  @ParameterizedTest
  @MethodSource("invalidCashGames")
  void givenInvalidCashGame_whenValidateGameExecuted_thenShouldThrowExceptions(final CashGame game) {
    assertThatThrownBy(() -> calculationValidator.validateGame(game))
      .isInstanceOf(EntriesAndWithdrawalAmountsAreNotEqualException.class);
  }

  @Test
  void givenInvalidTournamentGame_whenValidateGameExecuted_thenShouldThrowExceptions() {
    final var tournamentGame = GameCreator.tournament(builder -> builder.finaleSummary(null));
    assertThatThrownBy(() -> calculationValidator.validateGame(tournamentGame))
      .isInstanceOf(FinaleSummaryNotFoundException.class);
  }

  private static Stream<Arguments> invalidBountyGames() {
    return Stream.of(
      Arguments.of(
        GameCreator.bounty(builder -> builder.bountyList(Collections.emptyList())),
        BountiesAndEntriesSizeAreNotEqualException.class),
      Arguments.of(
        GameCreator.bounty(builder -> builder.bountyList(List.of(BountyCreator.bounty(), BountyCreator.bounty()))),
        BountiesAndEntriesSizeAreNotEqualException.class),
      Arguments.of(
        GameCreator.bounty(builder -> builder.finaleSummary(null)), FinaleSummaryNotFoundException.class)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidBountyGames")
  void givenInvalidBountyGame_whenValidateGameExecuted_thenShouldThrowExceptions(final BountyGame game,
                                                                                 final Class<BusinessException> exceptionClass) {
    assertThatThrownBy(() -> calculationValidator.validateGame(game))
      .isInstanceOf(exceptionClass);
  }

}