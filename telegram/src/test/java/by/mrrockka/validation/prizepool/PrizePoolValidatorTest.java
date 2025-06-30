package by.mrrockka.validation.prizepool;

import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.domain.prize.PositionPrize;
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
        PrizePoolCreator.domain(builder -> builder.positionPrizes(Collections.emptyList()))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(new PositionPrize(1, BigDecimal.valueOf(99)))))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(
            new PositionPrize(1, BigDecimal.valueOf(66)),
            new PositionPrize(1, BigDecimal.valueOf(33))
          )))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(
            new PositionPrize(1, BigDecimal.valueOf(10)),
            new PositionPrize(2, BigDecimal.valueOf(1)),
            new PositionPrize(3, BigDecimal.valueOf(5))
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
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(new PositionPrize(2, BigDecimal.valueOf(100)))))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(
            new PositionPrize(1, BigDecimal.valueOf(66)),
            new PositionPrize(1, BigDecimal.valueOf(34))
          )))
      ),
      Arguments.of(
        PrizePoolCreator.domain(builder -> builder.positionPrizes(
          List.of(
            new PositionPrize(1, BigDecimal.valueOf(70)),
            new PositionPrize(2, BigDecimal.valueOf(20)),
            new PositionPrize(4, BigDecimal.valueOf(10))
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