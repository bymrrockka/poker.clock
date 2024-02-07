package by.mrrockka.integration.repo.creator;

import by.mrrockka.domain.game.GameType;
import by.mrrockka.repo.game.GameEntity;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class GameCreator {

  private final static Faker FAKER = new Faker();

  public static GameEntity gameEntity() {
    return gameEntity(null);
  }

  public static GameEntity gameEntity(Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
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
