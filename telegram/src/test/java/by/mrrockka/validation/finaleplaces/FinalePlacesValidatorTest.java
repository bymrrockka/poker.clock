package by.mrrockka.validation.finaleplaces;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.TelegramPersonCreator;
import by.mrrockka.domain.game.Game;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class FinalePlacesValidatorTest {

  private final FinalePlacesValidator validator = new FinalePlacesValidator();

  private static Stream<Arguments> validGameType() {
    return Stream.of(
      Arguments.of(GameCreator.tournament()),
      Arguments.of(GameCreator.bounty())
    );
  }

  @ParameterizedTest
  @MethodSource("validGameType")
  void givenTournamentTypeGame_whenValidateGameTypeExecuted_shouldNotThrowExceptions(final Game game) {
    assertThatCode(() -> validator.validateGameType(game)).doesNotThrowAnyException();
  }

  @Test
  void givenCashTypeGame_whenValidateGameTypeExecuted_shouldThrowExceptions() {
    final var game = GameCreator.cash();
    assertThatCode(() -> validator.validateGameType(game))
      .isInstanceOf(ProcessingRestrictedException.class);
  }

  @Test
  void givenEmptyPlaces_whenValidatePlacesExecuted_shoudlThrowException() {
    assertThatCode(() -> validator.validatePlaces(Map.of()))
      .isInstanceOf(FinalePlacesCannotBeEmptyException.class);
  }

  @Test
  void givenFilledPlaces_whenValidatePlacesExecuted_shoudlNotThrowException() {
    final var places = Map.of(1, TelegramPersonCreator.domain());
    assertThatCode(() -> validator.validatePlaces(places)).doesNotThrowAnyException();
  }
}