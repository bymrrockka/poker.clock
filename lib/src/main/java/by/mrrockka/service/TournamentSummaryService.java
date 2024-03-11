package by.mrrockka.service;

import by.mrrockka.domain.summary.TournamentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.nonNull;


@Service
@RequiredArgsConstructor
public class TournamentSummaryService {

  private final FinalePlacesService finalePlacesService;
  private final PrizePoolService prizePoolService;

  public TournamentSummary assembleTournamentSummary(final UUID gameId, final BigDecimal totalAmount) {
    final var finalePlaces = finalePlacesService.getByGameId(gameId);
    final var prizePool = prizePoolService.getByGameId(gameId);

    if (nonNull(finalePlaces) && nonNull(prizePool)) {
      return TournamentSummary.of(prizePool, finalePlaces, totalAmount);
    } else {
      return null;
    }
  }
}
