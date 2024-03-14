package by.mrrockka.domain.summary.finale;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.domain.prize.PrizePool;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public record FinaleSummary(@NonNull List<FinalePlaceSummary> finaleSummaries) {

  public static FinaleSummary of(@NonNull final PrizePool prizePool, @NonNull final FinalePlaces finalePlaces,
                                 @NonNull final BigDecimal totalAmount) {
    final var finaleSummaries = prizePool.positionAndPercentages()
      .stream()
      .map(percentageAndPosition ->
             FinalePlaceSummary.builder()
               .position(percentageAndPosition.position())
               .person(finalePlaces.getByPosition(percentageAndPosition.position())
                         .orElseThrow(() -> new NoPrizeForPositionException(percentageAndPosition.position())))
               .amount(prizePool.calculatePrizeAmountFor(percentageAndPosition.position(), totalAmount))
               .build())
      .toList();

    return new FinaleSummary(finaleSummaries);
  }

  private Optional<BigDecimal> getPrizeFor(final Person person) {
    return finaleSummaries().stream()
      .filter(finalePlaceSummary -> finalePlaceSummary.person().equals(person))
      .map(FinalePlaceSummary::amount)
      .findFirst();
  }

  public BigDecimal calculateSummaryAmount(final Person person, final BigDecimal playerTotal) {
    final var prizeAmount = getPrizeFor(person).orElse(BigDecimal.ZERO);
    return prizeAmount.subtract(playerTotal);
  }

}
