package by.mrrockka.creator;

import by.mrrockka.domain.payout.Payout;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public final class PayoutCreator {

  public static Payout payout() {
    return payout(null);
  }

  public static Payout payout(final Consumer<Payout.PayoutBuilder> bountyBuilderConsumer) {
    final var payoutBuilder = Payout.builder()
      .payers(List.of(PayerCreator.payer()))
      .personEntries(EntriesCreator.entries());

    if (nonNull(bountyBuilderConsumer))
      bountyBuilderConsumer.accept(payoutBuilder);

    return payoutBuilder.build();
  }

}
