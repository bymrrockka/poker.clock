package by.mrrockka.repo.moneytransfer;

import by.mrrockka.domain.payout.TransferType;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record MoneyTransferEntity(
  @NonNull
  UUID gameId,
  @NonNull
  UUID personId,
  @NonNull
  BigDecimal amount,
  @NonNull
  TransferType type
) {
}
