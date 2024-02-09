package by.mrrockka.domain.summary;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.domain.prize.PrizePool;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public record GameSummary(@NonNull List<FinalePlaceSummary> finaleSummaries) {

  //todo: consider refactoring to separate service in case there will be additional implementations
  public static GameSummary of(final PrizePool prizePool, final FinalePlaces finalePlaces, BigDecimal totalAmount) {
    final var finaleSummaries = prizePool.positionAndPercentages()
      .stream()
      .map(percentageAndPosition ->
             FinalePlaceSummary.builder()
               .position(percentageAndPosition.position())
               .person(finalePlaces.getByPosition(percentageAndPosition.position()).orElseThrow())
               .amount(prizePool.calculatePrizeAmountFor(percentageAndPosition.position(), totalAmount))
               .build())
      .toList();

    return new GameSummary(finaleSummaries);
  }

  public boolean isInPrizes(Person person) {
    return finaleSummaries().stream()
      .map(FinalePlaceSummary::person)
      .anyMatch(p -> p.equals(person));
  }

  public BigDecimal getPrizeFor(Person person) {
    return finaleSummaries().stream()
      .filter(finalePlaceSummary -> finalePlaceSummary.person().equals(person))
      .map(FinalePlaceSummary::amount)
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPersonException(person));
  }

}
