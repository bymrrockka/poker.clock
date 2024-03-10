package by.mrrockka.service;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.game.Game;
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
  private final PlayerService playerService;

  public void storeNewGame(@NonNull final Game game) {
    gameRepository.save(gameMapper.toEntity(game));
  }

  public Game retrieveGame(@NonNull final UUID gameId) {
    final var gameEntity = gameRepository.findById(gameId);
    final var players = playerService.getAllForGame(gameId);
    final var gameSummary = gameSummaryService.assembleGameSummary(gameId, calculateTotalAmount(players));

    return gameMapper.toDomain(gameEntity, players, gameSummary);
  }

  private BigDecimal calculateTotalAmount(final List<Player> players) {
    return players.stream()
      .map(player -> player.entries().total())
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

}
