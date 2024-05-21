package by.mrrockka.service;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.mapper.MoneyTransferMapper;
import by.mrrockka.repo.moneytransfer.MoneyTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

  private final MoneyTransferRepository moneyTransferRepository;
  private final MoneyTransferMapper moneyTransferMapper;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public void storeBatch(final Game game, final List<Payout> payouts) {
    moneyTransferRepository.saveAll(moneyTransferMapper.map(game.getId(), payouts), Instant.now());
  }

}
