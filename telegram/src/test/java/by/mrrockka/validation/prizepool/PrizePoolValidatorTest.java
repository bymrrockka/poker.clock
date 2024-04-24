package by.mrrockka.validation.prizepool;

import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.domain.prize.PositionAndPercentage;
import by.mrrockka.domain.prize.PrizePool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class PrizePoolValidatorTest {

  private final PrizePoolValidator validator = new PrizePoolValidator();

  @Test
  void givenValidPrizePool_whenValidateCalled_thenShouldNotThrowExceptions() {
    final var prizePool = PrizePoolCreator.domain();
    assertThatCode(() -> validator.validate(prizePool)).doesNotThrowAnyException();
  }

  private static Stream<Arguments> invalidPercentage() {
    return Stream.of(
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(Collections.emptyList()))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(new PositionAndPercentage(1, BigDecimal.valueOf(99)))))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(
            new PositionAndPercentage(1, BigDecimal.valueOf(66)),
            new PositionAndPercentage(1, BigDecimal.valueOf(33))
          )))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(
            new PositionAndPercentage(1, BigDecimal.valueOf(10)),
            new PositionAndPercentage(2, BigDecimal.valueOf(1)),
            new PositionAndPercentage(3, BigDecimal.valueOf(5))
          )))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidPercentage")
  void givenPrizePoolWithTotalPercentageNotEqualToHundred_whenValidateCalled_thenShouldNotThrowExceptions(
    final PrizePool prizePool) {
    assertThatCode(() -> validator.validate(prizePool))
      .isInstanceOf(PrizePoolPercentageIsNotEqualHundredException.class);
  }


  private static Stream<Arguments> invalidPositions() {
    return Stream.of(
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(new PositionAndPercentage(2, BigDecimal.valueOf(100)))))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(
            new PositionAndPercentage(1, BigDecimal.valueOf(66)),
            new PositionAndPercentage(1, BigDecimal.valueOf(34))
          )))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionAndPercentages(
          List.of(
            new PositionAndPercentage(1, BigDecimal.valueOf(70)),
            new PositionAndPercentage(2, BigDecimal.valueOf(20)),
            new PositionAndPercentage(4, BigDecimal.valueOf(10))
          )))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidPositions")
  void givenPrizePoolWithPositionsGaps_whenValidateCalled_thenShouldNotThrowExceptions(
    final PrizePool prizePool) {
    assertThatCode(() -> validator.validate(prizePool))
      .isInstanceOf(PrizePoolPositionsHasGapsException.class);
  }
}