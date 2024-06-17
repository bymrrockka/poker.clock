package by.mrrockka.service.statistics;

import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PlayerInGameStatisticsService {

  public PlayerInGameStatistics retrieveStatistics(final Game game, final String nickname) {
    return PlayerInGameStatistics.builder()
      .personEntries(getPersonEntries(game, nickname))
      .personWithdrawals(getPersonWithdrawals(game, nickname).orElse(null))
      .personBounties(getPersonBounties(game, nickname).orElse(null))
      .moneyInGame(calculateMoneyInGame(game, nickname))
      .build();
  }

  private BigDecimal calculateMoneyInGame(final Game game, final String nickname) {
    final var personEntries = getPersonEntries(game, nickname);
    final var personEntriesTotal = personEntries.total();

    if (game.isType(CashGame.class)) {
      final var personWithdrawalsTotal = getPersonWithdrawals(game, nickname)
        .map(PersonWithdrawals::total)
        .orElse(BigDecimal.ZERO);
      return personEntriesTotal.subtract(personWithdrawalsTotal);
    }

    if (game.isType(BountyGame.class)) {
      final var personBountiesTotal = getPersonBounties(game, nickname)
        .map(PersonBounties::totalTaken)
        .orElse(BigDecimal.ZERO);
      final var bountyTotal = game.asType(BountyGame.class).getBountyAmount()
        .multiply(BigDecimal.valueOf(personEntries.entries().size()));
      return personEntriesTotal.add(bountyTotal).subtract(personBountiesTotal);
    }

    return personEntriesTotal;
  }

  private Optional<PersonBounties> getPersonBounties(final Game game, final String nickname) {
    if (game.isType(BountyGame.class)) {
      final var person = getPersonEntries(game, nickname).person();

      final var bounties = game.asType(BountyGame.class).getBountyList().stream()
        .filter(bounty -> bounty.from().equals(person) || bounty.to().equals(person))
        .toList();

      return Optional.of(PersonBounties.builder()
                           .bounties(bounties)
                           .person(person)
                           .build());
    }

    return Optional.empty();
  }

  private Optional<PersonWithdrawals> getPersonWithdrawals(final Game game, final String nickname) {
    if (game.isType(CashGame.class)) {
      return game.asType(CashGame.class).getWithdrawals().stream()
        .filter(personWithdrawals -> personWithdrawals.person().getNickname().equals(nickname))
        .findFirst();
    }

    return Optional.empty();
  }

  private PersonEntries getPersonEntries(final Game game, final String nickname) {
    return game.getEntries().stream()
      .filter(personEntries -> personEntries.person().getNickname().equals(nickname))
      .findFirst()
      .orElseThrow(() -> new PersonIsNotInGameException(nickname));
  }

}
