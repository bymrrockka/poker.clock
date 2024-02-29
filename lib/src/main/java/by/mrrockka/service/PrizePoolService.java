package by.mrrockka.service;

import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.mapper.PrizePoolMapper;
import by.mrrockka.repo.prizepool.PrizePoolRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrizePoolService {

  private final PrizePoolRepository prizePoolRepository;
  private final PrizePoolMapper prizePoolMapper;

  public PrizePool getByGameId(@NonNull UUID gameId) {
    return prizePoolRepository.findByGameId(gameId)
      .map(prizePoolMapper::toDomain)
      .orElse(null);
  }

  public void store(@NonNull UUID gameId, @NonNull PrizePool prizePool) {
    prizePoolRepository.save(prizePoolMapper.toEntity(gameId, prizePool));
  }
}
