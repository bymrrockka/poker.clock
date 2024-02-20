package by.mrrockka.service;

import by.mrrockka.domain.summary.GameSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class GameSummaryService {

  private final FinalePlacesService finalePlacesService;
  private final PrizePoolService prizePoolService;

  public GameSummary assembleGameSummary(UUID gameId, BigDecimal totalAmount) {
    final var finalePlaces = finalePlacesService.get(gameId);
    final var prizePool = prizePoolService.getPrizePool(gameId);
    return GameSummary.of(prizePool, finalePlaces, totalAmount);
  }
}
