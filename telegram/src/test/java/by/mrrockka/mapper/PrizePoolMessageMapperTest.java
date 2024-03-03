package by.mrrockka.mapper;

import by.mrrockka.creator.PrizePoolCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
      )
    );
  }

  @ParameterizedTest
  @MethodSource("prizePoolsMessage")
  void givenPrizePoolCommand_whenAttemptToMap_shouldReturnValidPrizePool(String command) {
    final var expected = PrizePoolCreator.domain();

    assertThat(prizePoolMessageMapper.map(command))
      .isEqualTo(expected);
  }
}