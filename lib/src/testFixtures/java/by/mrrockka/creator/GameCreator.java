package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.GameType;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.summary.TournamentGameSummary;
import by.mrrockka.repo.game.GameEntity;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameCreator {

  private static final Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final GameType GAME_TYPE = GameType.TOURNAMENT;
  public static final BigDecimal BUY_IN = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final BigDecimal STACK = BigDecimal.valueOf(FAKER.number().numberBetween(1500, 30000));
  public static final BigDecimal BOUNTY = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final List<Entries> ENTRIES = List.of(EntriesCreator.entries());
  public static final List<Bounty> BOUNTIES = List.of(Bounty.builder().build());
  public static final TournamentGameSummary GAME_SUMMARY = new TournamentGameSummary(List.of());

  public static TournamentGame tournament() {
    return tournament(null);
  }

  public static TournamentGame tournament(final Consumer<TournamentGame.TournamentGameBuilder> gameBuilderConsumer) {
    final var gameBuilder = TournamentGame.tournamentBuilder()
      .id(ID)
      .buyIn(BUY_IN)
      .stack(STACK)
//      .bounty(BOUNTY)
      .entries(ENTRIES)
      .tournamentGameSummary(GAME_SUMMARY)
//      .bountyTransactions(BOUNTIES)
      ;

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static GameEntity entity() {
    return entity(null);
  }

  public static GameEntity entity(final Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
    final var gameEntityBuilder = GameEntity.builder()
      .id(ID)
      .gameType(GAME_TYPE)
      .stack(STACK)
      .buyIn(BUY_IN)
      .bounty(BOUNTY);

    if (nonNull(builderConsumer))
      builderConsumer.accept(gameEntityBuilder);

    return gameEntityBuilder.build();
  }
}
