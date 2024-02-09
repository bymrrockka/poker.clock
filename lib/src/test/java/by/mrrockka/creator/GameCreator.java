package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.GameType;
import by.mrrockka.domain.summary.GameSummary;
import by.mrrockka.repo.game.GameEntity;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class GameCreator {

  private static final Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final String CHAT_ID = FAKER.random().hex();
  public static final GameType GAME_TYPE = GameType.TOURNAMENT;
  public static final BigDecimal BUY_IN = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final BigDecimal STACK = BigDecimal.valueOf(FAKER.number().numberBetween(1500, 30000));
  public static final BigDecimal BOUNTY = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final List<Player> PLAYERS = List.of(PlayerCreator.player());
  public static final List<Bounty> BOUNTIES = List.of(Bounty.builder().build());
  public static final GameSummary GAME_SUMMARY = new GameSummary(List.of());

  public static Game domain() {
    return domain(null);
  }

  public static Game domain(final Consumer<Game.GameBuilder> gameBuilderConsumer) {
    final var gameBuilder = Game.builder()
      .id(ID)
      .chatId(CHAT_ID)
      .gameType(GAME_TYPE)
      .buyIn(BUY_IN)
      .stack(STACK)
      .bountyAmount(BOUNTY)
      .players(PLAYERS)
      .gameSummary(GAME_SUMMARY)
      .bounties(BOUNTIES);

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static GameEntity entity() {
    return entity(null);
  }

  public static GameEntity entity(Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
    final var gameEntityBuilder = GameEntity.builder()
      .id(ID)
      .chatId(CHAT_ID)
      .gameType(GAME_TYPE)
      .stack(STACK)
      .buyIn(BUY_IN)
      .bounty(BOUNTY);

    if (nonNull(builderConsumer))
      builderConsumer.accept(gameEntityBuilder);

    return gameEntityBuilder.build();
  }
}
