package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BountyMessageMapperTest {

  private final BountyMessageMapper bountyMessageMapper = new BountyMessageMapper();

  private static Stream<Arguments> bountyMessage() {
    return Stream.of(
      Arguments.of("/bounty @kinger kicked @queen", "queen", "kinger"),
      Arguments.of("/bounty     @kinger          kicked      @queen", "queen", "kinger"),
      Arguments.of("/bounty @queen kicked @jackas", "jackas", "queen")
    );
  }

  @ParameterizedTest
  @MethodSource("bountyMessage")
  void givenBountyMessage_whenMapAttempt_shouldParseToPair(final String command, final String left,
                                                           final String right) {
    final var actual = bountyMessageMapper.map(command);
    assertThat(actual.getLeft()).isEqualTo(left);
    assertThat(actual.getRight()).isEqualTo(right);
  }

  @Test
  void givenInvalidBountyMessage_whenMapAttempt_shouldThrowException() {
    final var message = "/bounty @kinger kicked";
    assertThatThrownBy(() -> bountyMessageMapper.map(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}