package by.mrrockka.validation.collection;

import by.mrrockka.creator.TelegramPersonCreator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class CollectionsValidatorTest {
  private static final String ENTITY = "entity";

  private final CollectionsValidator validator = new CollectionsValidator();

  @Test
  void givenEmptyMap_whenValidateMapIsNotEmptyExecuted_shoudlThrowException() {
    assertThatCode(() -> validator.validateMapIsNotEmpty(Map.of(), ENTITY))
      .isInstanceOf(EntityCouldNotBeEmptyException.class);
  }

  @Test
  void givenFilledMap_whenValidateMapIsNotEmpty_shoudlNotThrowException() {
    final var map = Map.of(1, TelegramPersonCreator.domain());
    assertThatCode(() -> validator.validateMapIsNotEmpty(map, ENTITY)).doesNotThrowAnyException();
  }

  @Test
  void givenFilledCollection_whenValidateCollectionIsNotEmptyExecuted_shoudlNotThrowException() {
    final var collection = List.of(TelegramPersonCreator.domain());
    assertThatCode(() -> validator.validateCollectionIsNotEmpty(collection, ENTITY)).doesNotThrowAnyException();
  }

  @Test
  void givenEmptyCollection_whenValidateCollectionIsNotEmptyExecuted_shoudlNotThrowException() {
    final var collection = List.of();
    assertThatCode(() -> validator.validateCollectionIsNotEmpty(collection, ENTITY))
      .isInstanceOf(EntityCouldNotBeEmptyException.class);
  }
}