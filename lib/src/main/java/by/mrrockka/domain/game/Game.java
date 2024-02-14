package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.summary.GameSummary;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

//todo: add child models for tournament, bounty and cash related data
@Builder
public record Game(

  @NonNull
  UUID id,
  @NonNull
  String chatId,
  @NonNull
  GameType gameType,
  BigDecimal buyIn,
  BigDecimal stack,
  BigDecimal bounty,
  @NonNull
  List<Player> players,
  GameSummary gameSummary,
  List<Bounty> bountyTransactions
) {
}
