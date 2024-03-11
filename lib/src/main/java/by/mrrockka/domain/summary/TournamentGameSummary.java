package by.mrrockka.domain.summary;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.domain.prize.PrizePool;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

//todo: move
public record TournamentGameSummary(@NonNull List<FinalePlaceSummary> finaleSummaries) {

  public static TournamentGameSummary of(final PrizePool prizePool, final FinalePlaces finalePlaces,
                                         final BigDecimal totalAmount) {
    final var finaleSummaries = prizePool.positionAndPercentages()
      .stream()
      .map(percentageAndPosition ->
             FinalePlaceSummary.builder()
               .position(percentageAndPosition.position())
               .person(finalePlaces.getByPosition(percentageAndPosition.position()).orElseThrow())
               .amount(prizePool.calculatePrizeAmountFor(percentageAndPosition.position(), totalAmount))
               .build())
      .toList();

    return new TournamentGameSummary(finaleSummaries);
  }

  public boolean isInPrizes(final Person person) {
    return finaleSummaries().stream()
      .map(FinalePlaceSummary::person)
      .anyMatch(pers -> pers.equals(person));
  }

  public BigDecimal getPrizeFor(final Person person) {
    return finaleSummaries().stream()
      .filter(finalePlaceSummary -> finalePlaceSummary.person().equals(person))
      .map(FinalePlaceSummary::amount)
      .findFirst()
      .orElseThrow(() -> new NoPrizeForPersonException(person));
  }

}
