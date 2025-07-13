package by.mrrockka.service;

import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.mapper.WithdrawalsMapper;
import by.mrrockka.repo.withdrawals.WithdrawalsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class WithdrawalsService {

  private final WithdrawalsRepository withdrawalsRepository;
  private final WithdrawalsMapper withdrawalsMapper;

  public void storeWithdrawal(final UUID gameId, final UUID personId, final BigDecimal amount,
                              final Instant createdAt) {
    withdrawalsRepository.save(gameId, personId, amount, createdAt);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void storeBatch(final UUID gameId, final List<UUID> personIds, final BigDecimal amount,
                         final Instant createdAt) {
    withdrawalsRepository.saveAll(gameId, personIds, amount, createdAt);
  }

  public List<PersonWithdrawals> getAllForGame(@NonNull final UUID gameId) {
    final var entities = withdrawalsRepository.findAllByGameId(gameId);

    return entities.stream()
      .map(withdrawalsMapper::toDomain)
      .toList();
  }


}
