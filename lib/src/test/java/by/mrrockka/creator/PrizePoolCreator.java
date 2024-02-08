package by.mrrockka.creator;

import by.mrrockka.repo.prizepool.PrizePoolEntity;
import com.github.javafaker.Faker;
import org.assertj.core.data.MapEntry;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

public class PrizePoolCreator {

  private final static Faker FAKER = new Faker();

  public static PrizePoolEntity prizePoolEntity() {
    return prizePoolEntity(null);
  }

  public static PrizePoolEntity prizePoolEntity(Consumer<PrizePoolEntity.PrizePoolEntityBuilder> builderConsumer) {
    final var prizePoolEntityBuilder = PrizePoolEntity.builder()
      .gameId(UUID.randomUUID())
      .schema(schema());

    if (nonNull(builderConsumer))
      builderConsumer.accept(prizePoolEntityBuilder);

    return prizePoolEntityBuilder.build();
  }

  private static Map<Integer, BigDecimal> schema() {
    return IntStream.range(1, 4)
                    .mapToObj(key -> MapEntry.entry(key, FAKER.number().numberBetween(10, 100)))
                    .collect(Collectors.toMap(MapEntry::getKey, entry -> BigDecimal.valueOf(entry.value)));
  }
}
