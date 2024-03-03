package by.mrrockka.service;

import by.mrrockka.domain.Player;
import by.mrrockka.mapper.PlayerMapper;
import by.mrrockka.repo.entries.EntriesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final EntriesRepository entriesRepository;
  private final PlayerMapper playerMapper;

  public List<Player> getAllForGame(@NonNull UUID gameId) {
    final var entries = entriesRepository.findAllByGameId(gameId);

    return entries.stream()
      .map(playerMapper::toPlayer)
      .toList();
  }

}
