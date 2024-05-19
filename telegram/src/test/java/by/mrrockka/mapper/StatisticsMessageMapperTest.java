package by.mrrockka.mapper;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

class StatisticsMessageMapperTest {

  private final StatisticsMessageMapper mapper = new StatisticsMessageMapper();


  private static Stream<Arguments> validScenarios() {
    return Stream.of(
      Arguments.of("/game_stats", StatisticsType.GAME),
      Arguments.of("/my_stats", StatisticsType.PLAYER_IN_GAME)
    );
  }

  @ParameterizedTest
  @MethodSource("validScenarios")
  void givenValidMessage_whenMapCalled_thenShouldReturnMappedDto(final String command, final StatisticsType type) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(command));
    final var actual = mapper.map(metadata);
    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual.metadata()).isEqualTo(metadata),
      () -> assertThat(actual.type()).isEqualTo(type)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"/gam_stats", "/gamee_stats", "/m_stats", "/mymy_stats"})
  void givenInvalidMessage_whenMapCalled_thenShouldThrowExxception(final String command) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(command));
    assertThatCode(() -> mapper.map(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}