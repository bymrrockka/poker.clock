package by.mrrockka.validation.finaleplaces;

import by.mrrockka.creator.TelegramPersonCreator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class FinalePlacesValidatorTest {

  private final FinalePlacesValidator validator = new FinalePlacesValidator();

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