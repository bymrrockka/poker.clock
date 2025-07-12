package by.mrrockka.domain.statistics;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.Player;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PlayerInGameStatistics(
  Player player,
  @NonNull List<BigDecimal> entries,
  List<BigDecimal> withdrawals,
  List<Bounty> bounties,
  @NonNull BigDecimal moneyInGame) {
}
