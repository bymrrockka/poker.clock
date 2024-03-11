package by.mrrockka.creator;

import by.mrrockka.domain.Withdrawals;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WithdrawalsCreator {

  public static Withdrawals withdrawals() {
    return withdrawals(null);
  }

  public static Withdrawals withdrawals(final Consumer<Withdrawals.WithdrawalsBuilder> withdrawalsBuilderConsumer) {
    final var withdrawalsBuilder = Withdrawals.builder()
      .person(PersonCreator.domainRandom())
      .withdrawals(List.of(BigDecimal.ONE));

    if (nonNull(withdrawalsBuilderConsumer))
      withdrawalsBuilderConsumer.accept(withdrawalsBuilder);

    return withdrawalsBuilder.build();
  }

}
