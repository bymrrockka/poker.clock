package by.mrrockka.service;

import by.mrrockka.domain.Player;
import by.mrrockka.mapper.PlayerMapper;
import by.mrrockka.repo.entries.EntriesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


//todo: add int tests
@Service
@RequiredArgsConstructor
public class PlayerService {

  private final EntriesRepository entriesRepository;
  private final PlayerMapper playerMapper;

  public List<Player> retrievePlayers(@NonNull UUID gameId) {
    final var gameEntries = entriesRepository.findAllByGameId(gameId);

    return gameEntries.stream()
      .map(playerMapper::toPlayer)
      .toList();
  }

}
