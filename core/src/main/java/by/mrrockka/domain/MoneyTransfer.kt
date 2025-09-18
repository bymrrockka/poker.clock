package by.mrrockka.domain;

import by.mrrockka.domain.payout.TransferType;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record MoneyTransfer(
  @NonNull
  UUID personId,
  @NonNull
  UUID gameId,
  @NonNull
  BigDecimal amount,
  @NonNull
  TransferType type
) {
}
