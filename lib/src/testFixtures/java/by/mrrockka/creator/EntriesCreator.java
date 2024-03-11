package by.mrrockka.creator;

import by.mrrockka.domain.entries.Entries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntriesCreator {

  public static Entries entries() {
    return entries(null);
  }

  public static Entries entries(final Consumer<Entries.EntriesBuilder> entriesBuilderConsumer) {
    final var entriesBuilder = Entries.builder()
      .person(PersonCreator.domain())
      .entries(List.of(BigDecimal.ONE));

    if (nonNull(entriesBuilderConsumer))
      entriesBuilderConsumer.accept(entriesBuilder);

    return entriesBuilder.build();
  }

}
