package by.mrrockka.service;

import by.mrrockka.domain.payout.Payout;
import by.mrrockka.mapper.MoneyTransferMapper;
import by.mrrockka.repo.moneytransfer.MoneyTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

  private final MoneyTransferRepository moneyTransferRepository;
  private final MoneyTransferMapper moneyTransferMapper;

  public void storeBatch(final UUID gameId, final List<Payout> payouts) {
    moneyTransferRepository.saveAll(moneyTransferMapper.map(gameId, payouts), Instant.now());
  }

}
