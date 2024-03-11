package by.mrrockka.service;

import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.mapper.GameMapper;
import by.mrrockka.repo.game.GameEntity;
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
  private final TournamentSummaryService tournamentSummaryService;
  private final EntriesService entriesService;
  private final WithdrawalsService withdrawalsService;

  public void storeTournamentGame(@NonNull final TournamentGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public void storeCashGame(@NonNull final CashGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public TournamentGame retrieveTournamentGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    final var entries = entriesService.getAllForGame(gameId);
    final var gameSummary = tournamentSummaryService.assembleTournamentSummary(gameId, calculateTotalAmount(entries));

    return gameMapper.toTournament(gameEntity, entries, gameSummary);
  }

  private TournamentGame assembleTournamentGame(@NonNull final GameEntity gameEntity) {
    final var entries = entriesService.getAllForGame(gameEntity.id());
    final var gameSummary = tournamentSummaryService.assembleTournamentSummary(gameEntity.id(),
                                                                               calculateTotalAmount(entries));
    return gameMapper.toTournament(gameEntity, entries, gameSummary);
  }

  public CashGame retrieveCashGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    final var entries = entriesService.getAllForGame(gameId);
    final var withdrawals = withdrawalsService.getAllForGame(gameId);

    return gameMapper.toCash(gameEntity, entries, withdrawals);
  }

  private CashGame assembleCashGame(@NonNull final GameEntity gameEntity) {
    final var entries = entriesService.getAllForGame(gameEntity.id());
    final var withdrawals = withdrawalsService.getAllForGame(gameEntity.id());

    return gameMapper.toCash(gameEntity, entries, withdrawals);
  }

  public Game retrieveGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    return switch (gameEntity.gameType()) {
      case TOURNAMENT -> assembleTournamentGame(gameEntity);
      case CASH -> assembleCashGame(gameEntity);
      case BOUNTY -> assembleTournamentGame(gameEntity);
    };
  }

  private BigDecimal calculateTotalAmount(final List<Entries> entries) {
    return entries.stream()
      .map(Entries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

}
