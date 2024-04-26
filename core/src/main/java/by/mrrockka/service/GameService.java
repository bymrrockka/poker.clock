package by.mrrockka.service;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
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
  private final BountyService bountyService;

  public void storeTournamentGame(@NonNull final TournamentGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public void storeCashGame(@NonNull final CashGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public void storeBountyGame(@NonNull final BountyGame game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  //  todo: idea - inspect the repo for GameMetadata usage
  public Game retrieveGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    return switch (gameEntity.gameType()) {
      case TOURNAMENT -> assembleTournamentGame(gameEntity);
      case CASH -> assembleCashGame(gameEntity);
      case BOUNTY -> assembleBountyGame(gameEntity);
    };
  }

  private TournamentGame assembleTournamentGame(@NonNull final GameEntity gameEntity) {
    final var entries = entriesService.getAllForGame(gameEntity.id());
    final var gameSummary = tournamentSummaryService.assembleTournamentSummary(gameEntity.id(),
                                                                               calculateTotalAmount(entries));
    return gameMapper.toTournament(gameEntity, entries, gameSummary);
  }

  private BountyGame assembleBountyGame(@NonNull final GameEntity gameEntity) {
    final var entries = entriesService.getAllForGame(gameEntity.id());
    final var bountyList = bountyService.getAllForGame(gameEntity.id());
    final var gameSummary = tournamentSummaryService.assembleTournamentSummary(gameEntity.id(),
                                                                               calculateTotalAmount(entries));

    return gameMapper.toBounty(gameEntity, entries, bountyList, gameSummary);
  }

  private CashGame assembleCashGame(@NonNull final GameEntity gameEntity) {
    final var entries = entriesService.getAllForGame(gameEntity.id());
    final var withdrawals = withdrawalsService.getAllForGame(gameEntity.id());

    return gameMapper.toCash(gameEntity, entries, withdrawals);
  }

  private BigDecimal calculateTotalAmount(final List<PersonEntries> entries) {
    return entries.stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

}
