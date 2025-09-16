package by.mrrockka.creator;

import by.mrrockka.domain.MoneyTransfer;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.repo.moneytransfer.MoneyTransferEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public final class MoneyTransferCreator {

  public static final UUID PERSON_ID = UUID.randomUUID();
  public static final UUID GAME_ID = UUID.randomUUID();
  public static final BigDecimal AMOUNT = BigDecimal.TEN;
  public static final TransferType TYPE = TransferType.EQUAL;

  public static MoneyTransferEntity entity() {
    return entity(null);
  }

  public static MoneyTransferEntity entity(
    final Consumer<MoneyTransferEntity.MoneyTransferEntityBuilder> builderConsumer) {
    final var moneyTransferEntityBuilder = MoneyTransferEntity.builder()
      .personId(PERSON_ID)
      .gameId(GAME_ID)
      .amount(AMOUNT)
      .type(TYPE);

    if (nonNull(builderConsumer))
      builderConsumer.accept(moneyTransferEntityBuilder);

    return moneyTransferEntityBuilder.build();
  }

  public static MoneyTransfer debit() {
    return domain(builder -> builder.type(TransferType.DEBIT));
  }

  public static MoneyTransfer credit() {
    return domain(builder -> builder.type(TransferType.CREDIT));
  }

  public static MoneyTransfer equal() {
    return domain(builder -> builder.type(TransferType.EQUAL));
  }

  public static MoneyTransfer domain(final Consumer<MoneyTransfer.MoneyTransferBuilder> builderConsumer) {
    final var payoutBuilder = MoneyTransfer.builder()
      .personId(PERSON_ID)
      .gameId(GAME_ID)
      .amount(AMOUNT)
      .type(TYPE);

    if (nonNull(builderConsumer))
      builderConsumer.accept(payoutBuilder);

    return payoutBuilder.build();
  }

}
