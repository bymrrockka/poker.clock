package by.mrrockka.creator;

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

  private final static Faker FAKER = new Faker();

  public static Game domain() {
    return domain(null);
  }

  public static Game domain(final Consumer<Game.GameBuilder> gameBuilderConsumer) {
    final var gameBuilder = Game.builder()
      .id(UUID.randomUUID())
      .chatId(FAKER.random().hex())
      .gameType(GameType.TOURNAMENT)
      .buyIn(BigDecimal.valueOf(FAKER.number().numberBetween(10, 100)))
      .stack(BigDecimal.valueOf(FAKER.number().numberBetween(1500, 30000)))
      .players(List.of(PlayerCreator.player()))
      .gameSummary(new GameSummary(List.of()));

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static GameEntity entity() {
    return entity(null);
  }

  public static GameEntity entity(Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
    final var gameEntityBuilder = GameEntity.builder()
      .id(UUID.randomUUID())
      .chatId(FAKER.random().hex())
      .gameType(GameType.TOURNAMENT)
      .stack(BigDecimal.valueOf(FAKER.number().numberBetween(100, 1000)))
      .buyIn(BigDecimal.valueOf(FAKER.number().numberBetween(10, 100)))
      .bounty(BigDecimal.valueOf(FAKER.number().numberBetween(10, 100)));

    if (nonNull(builderConsumer))
      builderConsumer.accept(gameEntityBuilder);

    return gameEntityBuilder.build();
  }
}
