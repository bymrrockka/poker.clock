package by.mrrockka.creator;

import by.mrrockka.domain.prize.PositionPrize;
import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.repo.prizepool.PrizePoolEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PrizePoolCreator {

  public static final UUID GAME_ID = UUID.randomUUID();

  public static PrizePoolEntity entity() {
    return entity(null);
  }

  public static PrizePoolEntity entity(Consumer<PrizePoolEntity.PrizePoolEntityBuilder> builderConsumer) {
    final var prizePoolEntityBuilder = PrizePoolEntity.builder()
      .gameId(GAME_ID)
      .schema(schema());

    if (nonNull(builderConsumer))
      builderConsumer.accept(prizePoolEntityBuilder);

    return prizePoolEntityBuilder.build();
  }

  public static PrizePool domain() {
    return domain(null);
  }

  public static PrizePool domain(Consumer<PrizePool.PrizePoolBuilder> builderConsumer) {
    final var prizePoolBuilder = PrizePool.builder()
      .positionPrizes(positionsAndPercentage());

    if (nonNull(builderConsumer))
      builderConsumer.accept(prizePoolBuilder);

    return prizePoolBuilder.build();
  }

  private static Map<Integer, BigDecimal> schema() {
    return Map.of(
      1, BigDecimal.valueOf(60),
      2, BigDecimal.valueOf(30),
      3, BigDecimal.valueOf(10)
    );
  }

  private static List<PositionPrize> positionsAndPercentage() {
    return List.of(
      new PositionPrize(1, BigDecimal.valueOf(60)),
      new PositionPrize(2, BigDecimal.valueOf(30)),
      new PositionPrize(3, BigDecimal.valueOf(10))
    );
  }
}
