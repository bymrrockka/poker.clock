package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.summary.GameSummary;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

//todo: add child models for tournament, bounty and cash related data

@SuperBuilder
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
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

}
