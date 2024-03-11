package by.mrrockka.domain.game;

import by.mrrockka.domain.Player;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract sealed class Game permits TournamentGame, CashGame, BountyGame {

  @NonNull
  protected UUID id;
  //  todo: replace type with game sub types
  @NonNull
  protected GameType gameType;
  @NonNull
  protected BigDecimal buyIn;
  @NonNull
  protected BigDecimal stack;
  protected List<Player> players;

  protected Game(@NonNull final UUID id, @NonNull final GameType gameType, @NonNull final BigDecimal buyIn,
                 @NonNull final BigDecimal stack, final List<Player> players) {
    this.id = id;
    this.gameType = gameType;
    this.buyIn = buyIn;
    this.stack = stack;
    this.players = players;
  }
}
