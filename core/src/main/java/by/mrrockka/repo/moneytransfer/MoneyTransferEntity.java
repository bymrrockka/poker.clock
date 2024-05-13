package by.mrrockka.repo.moneytransfer;

import by.mrrockka.domain.payout.TransferType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record MoneyTransferEntity(
  UUID gameId,
  UUID personId,
  BigDecimal amount,
  TransferType type
) {
}
