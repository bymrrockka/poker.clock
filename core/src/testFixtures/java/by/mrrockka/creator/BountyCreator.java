package by.mrrockka.creator;

import by.mrrockka.domain.Bounty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BountyCreator {

  public static final BigDecimal BOUNTY_AMOUNT = BigDecimal.ONE;

  public static List<Bounty> bountiesList(final int size, final BigDecimal bounty) {
    return IntStream.range(0, size)
      .mapToObj(i -> bounty(builder -> builder.amount(bounty)))
      .collect(Collectors.toList());
  }

  public static Bounty bounty() {
    return bounty(null);
  }

  public static Bounty bounty(final Consumer<Bounty.BountyBuilder> bountyBuilderConsumer) {
    final var bountyBuilder = Bounty.builder()
      .to(PersonCreator.domainRandom())
      .from(PersonCreator.domainRandom())
      .amount(BOUNTY_AMOUNT);

    if (nonNull(bountyBuilderConsumer))
      bountyBuilderConsumer.accept(bountyBuilder);

    return bountyBuilder.build();
  }

}
