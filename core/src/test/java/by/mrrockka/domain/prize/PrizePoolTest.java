package by.mrrockka.domain.prize;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrizePoolTest {

  private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(100);
  private static final int VALID_POSITION = 1;
  private static final int INVALID_POSITION = 2;
  private static final List<PositionPrize> PERCENTAGE_AND_POSITIONS =
    List.of(PositionPrize.builder()
              .position(VALID_POSITION)
              .percentage(BigDecimal.valueOf(100))
              .build());
  private static final PrizePool PRIZE_POOL = PrizePool.builder()
    .positionPrizes(PERCENTAGE_AND_POSITIONS)
    .build();

  @Test
  void givenPrizeAndPositionList_whenGetPrizeForExistingPositionExecuted_thenShouldReturnAmount() {

    assertThat(PRIZE_POOL.calculatePrizeAmountFor(VALID_POSITION, TOTAL_AMOUNT))
      .isEqualTo(TOTAL_AMOUNT);
  }

  @Test
  void givenPrizeAndPositionList_whenGetPrizeForNonExistingPositionExecuted_thenShouldReturnAmount() {
    assertThatThrownBy(() -> PRIZE_POOL.calculatePrizeAmountFor(INVALID_POSITION, TOTAL_AMOUNT))
      .isInstanceOf(NoPrizeForPositionException.class);
  }

  @Test
  void givenPrizeAndPositionList_whenIsInPrizesWithExistingPositionExecuted_thenShouldReturnAmount() {
    assertThat(PRIZE_POOL.isInPrizes(VALID_POSITION)).isTrue();
  }

  @Test
  void givenPrizeAndPositionList_whenIsInPrizesWithNonExistingPositionExecuted_thenShouldReturnAmount() {
    assertThat(PRIZE_POOL.isInPrizes(INVALID_POSITION)).isFalse();
  }

}