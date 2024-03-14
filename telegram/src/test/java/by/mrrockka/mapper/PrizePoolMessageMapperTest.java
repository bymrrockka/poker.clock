package by.mrrockka.mapper;

import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrizePoolMessageMapperTest {

  private final PrizePoolMessageMapper prizePoolMessageMapper = new PrizePoolMessageMapper();

  private static Stream<Arguments> prizePoolsMessage() {
    return Stream.of(
      Arguments.of(
        """
          /prizepool
          1 60%
          2 30%
          3 10%
          """
      ),
      Arguments.of(
        """
          /prizepool
          1. 60%
          2. 30%
          3. 10%
          """
      ),
      Arguments.of(
        """
          /prizepool
          1 - 60%
          2 - 30%
          3 - 10%
          """
      ),
      Arguments.of(
        """
          /prizepool
          1 60%, 2. 30%,3 - 10%
          """
      ),
      Arguments.of(
        """
          /prizepool
          1 :60%, 2.= 30%,
          3=10%
          """
      ),
      Arguments.of("/prizepool 1 :60%, 2.= 30%, 3=10%"),
      Arguments.of("/prizepool 1 60, 2 30, 3 10")
    );
  }

  @ParameterizedTest
  @MethodSource("prizePoolsMessage")
  void givenPrizePoolCommand_whenAttemptToMap_shouldReturnValidPrizePool(final String command) {
    final var expected = PrizePoolCreator.domain();

    assertThat(prizePoolMessageMapper.map(command))
      .isEqualTo(expected);
  }


  private static Stream<Arguments> invalidMessage() {
    return Stream.of(
      Arguments.of("/prizepool 160%, 230%, 310%"),
      Arguments.of("/prizepool 60, 30, 10"),
      Arguments.of("/prizepool\n")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidMessage")
  void givenInvalidPrizePoolMessage_whenMapAttempt_shouldThrowException(final String message) {
    assertThatThrownBy(() -> prizePoolMessageMapper.map(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}