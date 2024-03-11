package by.mrrockka.repo.withdrawals;

import by.mrrockka.repo.person.PersonEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record WithdrawalsEntity(
  UUID gameId,
  PersonEntity person,
  List<BigDecimal> amounts
) {
}
