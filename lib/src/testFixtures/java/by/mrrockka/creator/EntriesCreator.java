package by.mrrockka.creator;

import by.mrrockka.domain.entries.Entries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntriesCreator {

  public static List<Entries> entriesList(final int size, BigDecimal buyin) {
    return IntStream.range(0, size)
      .mapToObj(i -> entries(builder -> builder.entries(List.of(buyin))))
      .toList();
  }

  public static Entries entries() {
    return entries(null);
  }

  public static Entries entries(final Consumer<Entries.EntriesBuilder> entriesBuilderConsumer) {
    final var entriesBuilder = Entries.builder()
      .person(PersonCreator.domainRandom())
      .entries(List.of(BigDecimal.ONE));

    if (nonNull(entriesBuilderConsumer))
      entriesBuilderConsumer.accept(entriesBuilder);

    return entriesBuilder.build();
  }

}
