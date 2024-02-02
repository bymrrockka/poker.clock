package by.mrrockka.integration.repo.creator;

import by.mrrockka.domain.game.GameType;
import by.mrrockka.repo.entities.GameEntity;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class GameCreator {

  public static GameEntity gameEntity() {
    return gameEntity(null);
  }

  public static GameEntity gameEntity(Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
    final var gameEntityBuilder = GameEntity.builder()
                                            .id(UUID.randomUUID())
                                            .chatId(UUID.randomUUID().toString())
                                            .gameType(GameType.TOURNAMENT)
                                            .stack(BigDecimal.TEN)
                                            .buyIn(BigDecimal.ONE);

    if (nonNull(builderConsumer))
      builderConsumer.accept(gameEntityBuilder);

    return gameEntityBuilder.build();
  }
}
