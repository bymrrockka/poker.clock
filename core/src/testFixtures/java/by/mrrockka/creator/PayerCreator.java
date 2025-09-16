package by.mrrockka.creator;

import by.mrrockka.domain.payout.Payer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public final class PayerCreator {

  public static final BigDecimal DEBT_AMOUNT = BigDecimal.ONE;

  public static Payer payer() {
    return payer(null);
  }

  public static Payer payer(final Consumer<Payer.PayerBuilder> payerBuilderConsumer) {
    final var payerBuilder = Payer.builder()
      .personEntries(EntriesCreator.entries())
      .amount(DEBT_AMOUNT);

    if (nonNull(payerBuilderConsumer))
      payerBuilderConsumer.accept(payerBuilder);

    return payerBuilder.build();
  }

}
