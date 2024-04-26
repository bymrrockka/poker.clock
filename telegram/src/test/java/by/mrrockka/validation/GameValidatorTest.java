package by.mrrockka.validation;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.domain.game.Game;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class GameValidatorTest {

  private final GameValidator validator = new GameValidator();

  private static Stream<Arguments> tournamentType() {
    return Stream.of(
      Arguments.of(GameCreator.tournament()),
      Arguments.of(GameCreator.bounty())
    );
  }

  @ParameterizedTest
  @MethodSource("tournamentType")
  void givenTournamentTypeGame_whenvalidateGameIsTournamentTypeExecuted_shouldNotThrowExceptions(final Game game) {
    assertThatCode(() -> validator.validateGameIsTournamentType(game)).doesNotThrowAnyException();
  }

  @Test
  void givenCashTypeGame_whenvalidateGameIsCashTypeExecuted_shouldNotThrowExceptions() {
    final var game = GameCreator.cash();
    assertThatCode(() -> validator.validateGameIsCashType(game)).doesNotThrowAnyException();
  }

  @Test
  void givenCashTypeGame_whenvalidateGameIsTournamentTypeExecuted_shouldThrowExceptions() {
    final var game = GameCreator.cash();
    assertThatCode(() -> validator.validateGameIsTournamentType(game))
      .isInstanceOf(ProcessingRestrictedException.class);
  }

  @ParameterizedTest
  @MethodSource("tournamentType")
  void givenTournamentTypeGame_whenvalidateGameIsCashTypeExecuted_shouldThrowExceptions(final Game game) {
    assertThatCode(() -> validator.validateGameIsCashType(game))
      .isInstanceOf(ProcessingRestrictedException.class);
  }
}