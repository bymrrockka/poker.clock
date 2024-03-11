package by.mrrockka.service;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.mapper.GameMapper;
import by.mrrockka.repo.game.GameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameMapper gameMapper;
  private final GameRepository gameRepository;
  private final GameSummaryService gameSummaryService;
  private final EntriesService entriesService;

  public void storeNewGame(@NonNull final TournamentGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public TournamentGame retrieveGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    final var entries = entriesService.getAllForGame(gameId);
    final var gameSummary = gameSummaryService.assembleGameSummary(gameId, calculateTotalAmount(entries));

    return gameMapper.toDomain(gameEntity, entries, gameSummary);
  }

  private BigDecimal calculateTotalAmount(final List<Entries> entries) {
    return entries.stream()
      .map(Entries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

}
