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
  private static final List<PercentageAndPosition> PERCENTAGE_AND_POSITIONS = List.of(PercentageAndPosition.builder()
                                                                                        .position(VALID_POSITION)
                                                                                        .percentage(
                                                                                          BigDecimal.valueOf(100))
                                                                                        .build());

  private static final PrizePool PRIZE_POOL = PrizePool.builder()
    .percentageAndPositions(PERCENTAGE_AND_POSITIONS)
    .totalBuyInsAmount(TOTAL_AMOUNT)
    .build();

  @Test
  void givenPrizeAndPositionList_whenGetPrizeForExistingPositionExecuted_thenShouldReturnAmount() {

    assertThat(PRIZE_POOL.getPrizeFor(VALID_POSITION)).isEqualTo(TOTAL_AMOUNT);
  }

  @Test
  void givenPrizeAndPositionList_whenGetPrizeForNonExistingPositionExecuted_thenShouldReturnAmount() {
    assertThatThrownBy(() -> PRIZE_POOL.getPrizeFor(INVALID_POSITION))
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

  @Test
  void givenNullablePrizeAndPosition_whenMethodExecuted_thenShouldThrowNullPointer() {
    final var prizePool = new PrizePool(null, TOTAL_AMOUNT);
    assertThatThrownBy(() -> prizePool.getPrizeFor(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Prize and Position list cannot be null");
    assertThatThrownBy(() -> prizePool.isInPrizes(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Prize and Position list cannot be null");
  }

  @Test
  void givenNullableTotalAmount_whenMethodExecuted_thenShouldThrowNullPointer() {
    final var prizePool = new PrizePool(PERCENTAGE_AND_POSITIONS, null);
    assertThatThrownBy(() -> prizePool.getPrizeFor(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Total Buy ins cannot be null");
    assertThatThrownBy(() -> prizePool.isInPrizes(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Total Buy ins cannot be null");
  }

}