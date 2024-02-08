package by.mrrockka.domain.summary;

import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.domain.player.Person;
import by.mrrockka.domain.prize.PrizePool;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public record GameSummary(@NonNull List<FinaleSummary> finaleSummaries) {

  //todo: consider refactoring to separate service in case there will be additional implementations
  public static GameSummary of(final PrizePool prizePool, final FinalePlaces finalePlaces) {
    final var finaleSummaries = prizePool.percentageAndPositions()
      .stream()
      .map(percentageAndPosition ->
             FinaleSummary.builder()
               .position(percentageAndPosition.position())
               .person(finalePlaces.getByPosition(percentageAndPosition.position()).orElseThrow())
               .amount(prizePool.getPrizeFor(percentageAndPosition.position()))
               .build())
      .toList();

    return new GameSummary(finaleSummaries);
  }

  public boolean isInPrizes(Person person) {
    return finaleSummaries().stream()
      .map(FinaleSummary::person)
      .anyMatch(p -> p.equals(person));
  }

  public BigDecimal getPrizeFor(Person person) {
    return finaleSummaries().stream()
      .filter(finaleSummary -> finaleSummary.person().equals(person))
      .map(FinaleSummary::amount)
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPersonException(person));
  }

}
