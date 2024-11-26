package by.mrrockka.parser;

import by.mrrockka.creator.PrizePoolCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrizePoolMessageParserTest {

  private final PrizePoolMessageParser prizePoolMessageParser = new PrizePoolMessageParser();

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
  void givenPrizePoolCommand_whenAttemptToParse_shouldReturnValidPrizePool(final String command) {
    final var expected = PrizePoolCreator.domain();

    assertThat(prizePoolMessageParser.parse(command))
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
  void givenInvalidPrizePoolMessage_whenParseAttempt_shouldThrowException(final String message) {
    assertThatThrownBy(() -> prizePoolMessageParser.parse(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}