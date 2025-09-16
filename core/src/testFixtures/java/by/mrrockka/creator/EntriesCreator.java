package by.mrrockka.creator;

import by.mrrockka.domain.collection.PersonEntries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public final class EntriesCreator {

  public static List<PersonEntries> entriesList(final int size, BigDecimal buyin) {
    return IntStream.range(0, size)
      .mapToObj(i -> entries(builder -> builder.entries(List.of(buyin))))
      .collect(Collectors.toList());
  }

  public static PersonEntries entries() {
    return entries(null);
  }

  public static PersonEntries entries(final Consumer<PersonEntries.PersonEntriesBuilder> entriesBuilderConsumer) {
    final var entriesBuilder = PersonEntries.builder()
      .person(PersonCreator.domainRandom())
      .entries(List.of(BigDecimal.ONE));

    if (nonNull(entriesBuilderConsumer))
      entriesBuilderConsumer.accept(entriesBuilder);

    return entriesBuilder.build();
  }

}
