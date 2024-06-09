package by.mrrockka.creator;

import by.mrrockka.domain.collection.PersonWithdrawals;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WithdrawalsCreator {

  public static List<PersonWithdrawals> withdrawalsList(final int size, BigDecimal buyin) {
    return IntStream.range(0, size)
      .mapToObj(i -> withdrawals(builder -> builder.withdrawals(List.of(buyin))))
      .collect(Collectors.toList());
  }

  public static PersonWithdrawals withdrawals() {
    return withdrawals(null);
  }

  public static PersonWithdrawals withdrawals(
    final Consumer<PersonWithdrawals.PersonWithdrawalsBuilder> withdrawalsBuilderConsumer) {
    final var withdrawalsBuilder = PersonWithdrawals.builder()
      .person(PersonCreator.domainRandom())
      .withdrawals(List.of(BigDecimal.ONE));

    if (nonNull(withdrawalsBuilderConsumer))
      withdrawalsBuilderConsumer.accept(withdrawalsBuilder);

    return withdrawalsBuilder.build();
  }

}
