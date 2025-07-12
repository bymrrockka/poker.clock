package by.mrrockka.validation.prizepool;

import by.mrrockka.domain.prize.PositionPrize;
import by.mrrockka.domain.prize.PrizePool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;

@Deprecated(forRemoval = true)
@Component
public class PrizePoolValidator {

  public void validate(final PrizePool prizePool) {
    validatePercentage(prizePool);
    validatePositions(prizePool);
  }

  private void validatePercentage(final PrizePool prizePool) {
    final var totalPercentage = prizePool.positionPrizes().stream()
      .map(PositionPrize::percentage)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
      throw new PrizePoolPercentageIsNotEqualHundredException();
    }
  }

  private void validatePositions(final PrizePool prizePool) {
    final var pps = prizePool.positionPrizes()
      .stream()
      .sorted(Comparator.comparing(PositionPrize::position))
      .toList();

    for (int i = 0; i < pps.size(); i++) {
      final var pp = pps.get(i);
      if (i + 1 != pp.position()) {
        throw new PrizePoolPositionsHasGapsException(i + 1);
      }
    }
  }

}
