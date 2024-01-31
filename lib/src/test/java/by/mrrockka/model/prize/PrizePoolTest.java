package by.mrrockka.model.prize;

import by.mrrockka.domain.prize.NoPrizeForPositionException;
import by.mrrockka.domain.prize.PrizeAndPosition;
import by.mrrockka.domain.prize.PrizePool;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrizePoolTest {

  private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(100);
  private static final int VALID_POSITION = 1;
  private static final int INVALID_POSITION = 2;
  private static final List<PrizeAndPosition> PRIZE_AND_POSITION_LIST = List.of(PrizeAndPosition.builder()
    .place(VALID_POSITION)
    .prize(BigDecimal.valueOf(100))
    .build());

  private static final PrizePool PRIZE_POOL = PrizePool.builder()
    .prizeAndPositionList(PRIZE_AND_POSITION_LIST)
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
    final var prizePool = new PrizePool(PRIZE_AND_POSITION_LIST, null);
    assertThatThrownBy(() -> prizePool.getPrizeFor(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Total Buy ins cannot be null");
    assertThatThrownBy(() -> prizePool.isInPrizes(VALID_POSITION))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Total Buy ins cannot be null");
  }

}