package by.mrrockka.service;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.mapper.WithdrawalsMapper;
import by.mrrockka.repo.withdrawals.WithdrawalsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawalsService {

  private final WithdrawalsRepository withdrawalsRepository;
  private final WithdrawalsMapper withdrawalsMapper;

  public void storeWithdrawal(final UUID gameId, final UUID personId, final BigDecimal amount,
                              final Instant createdAt) {
    withdrawalsRepository.save(gameId, personId, amount, createdAt);
  }

  public List<Withdrawals> getAllForGame(@NonNull final UUID gameId) {
    final var entities = withdrawalsRepository.findAllByGameId(gameId);

    return entities.stream()
      .map(withdrawalsMapper::toDomain)
      .toList();
  }


}
