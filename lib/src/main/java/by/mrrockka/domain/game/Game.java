package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.summary.GameSummary;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

//todo: add child models for tournament, bounty and cash related data


@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Game {

  @NonNull
  UUID id;
  @NonNull
  GameType gameType;
  @NonNull
  BigDecimal buyIn;
  @NonNull
  BigDecimal stack;
  BigDecimal bounty;
  List<Player> players;
  GameSummary gameSummary;
  List<Bounty> bountyTransactions;

  @Builder(builderMethodName = "gameBuilder")
  public Game(@NonNull UUID id, @NonNull GameType gameType, @NonNull BigDecimal buyIn, @NonNull BigDecimal stack,
              BigDecimal bounty, List<Player> players, GameSummary gameSummary, List<Bounty> bountyTransactions) {
    this.id = id;
    this.gameType = gameType;
    this.buyIn = buyIn;
    this.stack = stack;
    this.bounty = bounty;
    this.players = players;
    this.gameSummary = gameSummary;
    this.bountyTransactions = bountyTransactions;
  }
}
