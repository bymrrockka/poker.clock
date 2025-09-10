package by.mrrockka.service;

import by.mrrockka.repo.game.GameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Deprecated(forRemoval = true)
@Service
@RequiredArgsConstructor
public class GameServiceOld {

  private final GameRepository gameRepository;

  public void finishGame(@NonNull final by.mrrockka.domain.Game game) {
    if (gameRepository.findById(game.getId()).finishedAt() != null) {
      gameRepository.finish(game.getId(), Instant.now());
    }
  }

}
