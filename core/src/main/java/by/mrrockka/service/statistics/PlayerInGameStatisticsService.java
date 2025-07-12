package by.mrrockka.service.statistics;

import by.mrrockka.domain.*;
import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

//todo: refactor
@Component
public class PlayerInGameStatisticsService {

  public PlayerInGameStatistics retrieveStatistics(final Game game, final String nickname) {
    var player = game.getPlayers().stream()
      .filter(
        pl -> pl.getPerson().getNickname().equals(nickname))
      .findFirst()
      .orElseThrow();

    return PlayerInGameStatistics.builder()
      .player(player)
      .entries(getPersonEntries(game, nickname))
      .withdrawals(getPersonWithdrawals(game, nickname))
      .bounties(getPersonBounties(game, nickname))
      .moneyInGame(calculateMoneyInGame(game, nickname))
      .build();
  }

  private BigDecimal calculateMoneyInGame(final Game game, final String nickname) {
    final var personEntries = getPersonEntries(game, nickname);
    final var personEntriesTotal = personEntries
      .stream()
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (game instanceof CashGame) {
      final var personWithdrawalsTotal = getPersonWithdrawals(game, nickname)
        .stream()
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
      return personEntriesTotal.subtract(personWithdrawalsTotal);
    }

    if (game instanceof BountyTournamentGame) {
      final var personBountiesTotal = getPersonBounties(game, nickname)
        .stream()
        .map(Bounty::getAmount)
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
      final var bountyTotal = ((BountyTournamentGame) game).getBounty()
        .multiply(BigDecimal.valueOf(personEntries.size()));
      return personEntriesTotal.add(bountyTotal).subtract(personBountiesTotal);
    }

    return personEntriesTotal;
  }

  private List<Bounty> getPersonBounties(final Game game, final String nickname) {
    if (game instanceof BountyTournamentGame) {
      return game.getPlayers().stream()
        .map(player -> (BountyPlayer) player)
        .flatMap(player -> player.getBounties().stream())
        .filter(bounty -> bounty.getTo().getNickname().equals(nickname))
        .toList();
    }
    return Collections.emptyList();
  }

  private List<BigDecimal> getPersonWithdrawals(final Game game, final String nickname) {
    if (game instanceof CashGame) {
      return game.getPlayers().stream()
        .filter(player -> player.getPerson().getNickname().equals(nickname))
        .map(player -> (CashPlayer) player)
        .flatMap(cashPlayer -> cashPlayer.getWithdrawals().stream())
        .toList();
    }

    return Collections.emptyList();
  }

  private List<BigDecimal> getPersonEntries(final Game game, final String nickname) {
    return game.getPlayers().stream()
      .filter(personEntries -> personEntries.getPerson().getNickname().equals(nickname))
      .flatMap(player -> player.getEntries().stream())
      .toList();
  }

}
