package by.mrrockka.service;

import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.mapper.FinalePlacesMapper;
import by.mrrockka.repo.finalplaces.FinalePlacesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinalePlacesService {

  private final FinalePlacesRepository finalePlacesRepository;
  private final FinalePlacesMapper finalePlacesMapper;

  public FinalePlaces getByGameId(@NonNull final UUID gameId) {
    return finalePlacesRepository.findByGameId(gameId)
      .map(finalePlacesMapper::toDomain)
      .orElse(null);
  }

  public void store(@NonNull final UUID gameId, @NonNull final FinalePlaces finalePlaces) {
    finalePlacesRepository.save(finalePlacesMapper.toEntity(gameId, finalePlaces));
  }
}
